#Glace Dynamic Web Flow (GDWF)

##Description
Glace Dynamic Web Flow plugin is developed to make Grails implementation of a web flow flexible and dynamic. The plugin inherits Spring Web Flow paradigms and utilizes principle of decentralised configuration. This approach allows to override sequence of flow steps and affect transition rules on runtime.

##GDWF Concepts

 * State in GDWF is not bound to certain flow and exists as a separate unit, therefore every declared state can be reused in different flows.
 * Every state is self-contained and, unlike specific action or view states in Spring, can solve any kind of web flow tasks.
 * Transitions between states are highly adjustable, in GDWF every flow state can affect further flow transitions.

##Installation

  1. Download and unpack the [code](#)
  1. Install the plugin into local Maven cache: `grails maven-install`
  1. Declare dependency on the plugin in application *grails-app/conf/BuildConfig.groovy* file: `compile ':glace-dynamic-web-flow:0.1.0'`

##Basic Usage

####States

As it is known, a web flow consists of a number of steps called "states". In GDWF states manage the flow by handling *activity* and *event* entities:

 * Event -- user action handler, processes events triggered by a view.
 * Activity -- a single unit of work, it is executed by the state in case if there is no event to handle.

Each GDWF state exists in a separate controller action and may consist of *activity* and *event handlers* sections:

```groovy
def fooState() {
  // activity section
  activity {
    // activity code
  }
  // event handlers section
  bar {
    // 'bar' event handler
  }
  baz {
    // 'baz' event handler
  }
}
```

> Activity is not executed if user reloads HTML page.

There are two kinds of states in the GDWF plugin: *setup state* and *flow state*. States are declared using annotations `@FlowSetup` and `@FlowState`.

 * @FlowSetup -- registers annotated action as the setup state. A setup state serves as an entry point of the flow.
 * @FlowState -- registers annotated action as the regular flow state. This annotation takes the following parameters:
    - view (optional) - view to render instead of the default one

Setup state:

```groovy
@FlowSetup
def createUser() {
  activity {
    // activity code
  }
}
```

Flow state:

```groovy
@FlowState(view = '/user/details.gsp')
def setUserDetails() {
  next {
    // 'next' event handler
  }
}
```

> There can be only one setup state in a web flow.

There are a few significant differences between flow state and setup state:

 * Event handlers declared in a setup state are applied for all the states of the flow.
 * Setup state necessarily should contain an activity section, while in the flow state both sections are optional.
 * Setup state can not handle events itself, it always executes its activity, regardless of whether an event is triggered or not.

```groovy
@FlowSetup
def createUser() {
  activity {
    // activity code
  }
  cancel {
    // 'cancel' event handler
  }
}

@FlowState
def setUserDetails() {
  next {
    // 'next' event handler
  }
  // state also handles 'cancel' event, as it is declared in the flow setup state
}

@FlowState(view = '/user/address.gsp')
def setUserAddress() {
  save {
    // 'save' event handler
  }
  cancel {
    // overrides 'cancel' event handler defined in the setup state
  }
}
```

####Transition

Transitions between states are initiated via `route` method:

```groovy
@FlowSetup
def createUser() {
  activity {
    route(controller: 'user', action: 'setUserDetails')
  }
}
```

Route method signature is similar to Grails redirect method, there are, however, a few differences. Valid route method calls:

```groovy
route(action: 'setUserDetails')
```
```groovy
route(controller: 'user', action: 'setUserDetails')
```
```groovy
route(controller: 'user', action: 'setUserDetails', params: [id: params.id])
```
```groovy
route(controller: 'user', action: 'setUserDetails', activity: false)
```

Parameters:

 - action (required) -- state or action to route user to
 - controller (optional) -- action controller, if not specified, current controller will be linked
 - params (optional) -- a map that contains request parameters
 - activity (optional) -- if false, activity closure will not be executed when user is redirected to specified state, true by default.

It is required to exit a web flow by routing to regular controller action with 'route' or 'redirect' methods, in this way the plugin can free memory resources allocated for the flow execution: flush Hibernate session and declare the execution as completed. If user tries to re-enter a completed flow, he/she will be redirected to the setup state and new flow execution will be created.

Shortcuts:

An event handler which contains only route method:

```groovy
cancel {
  route(action: 'cancel')
}
```

can be replaced by the following:


```groovy
cancel action: 'cancel'
```

####Dispatching

In order to assure reusability of flow states, GDWF plugin provides dispatching mechanism that allows to override the exit points of the states.

For instance, here is a flow state which should be reusable in a number of different flows:

```groovy
class AddressController {

  @FlowState
  def setAddress() {
    next {
      // event handler code
      route(action: 'success')
    }
    cancel action: 'cancel'
  }
}
```

To use this state in a certain flow, it is necessary to declare a dispatcher which overrides arguments of the route method:

```groovy
def UserController {

  @FlowState
  def setUserDetails() {
    next {
      // route to 'setAddress' state
      route(controller: 'address', action: 'setAddress') {
        // map 'success' to 'saveUser' state
        map(action: 'success').to(controller: 'user', action: 'saveUser')
        // map 'cancel' to 'list' action
        map(action: 'cancel').to(controller: 'user', action: 'list')
      }
    }
  }

  @FlowState
  def saveUser() {
    activity {
      // activity code
    }
  }

  def list() {}
}
```

Expression, encapsulated in a closure and passed to the `route` method, is called dispatcher:

```groovy
  {
    map(action: 'success').to(controller: 'user', action: 'saveUser')
    map(action: 'cancel').to(controller: 'user', action: 'list')
  }
```

Dispatcher can contain one or several mappings. Mapping routes user to the state specified as argument of `to` method if routing to the action specified in arguments of `map` method happens.

`map` method parameters:

 - action (required) -- action name to override
 - controller (optional) -- controller name to override. If not specified, controller,
     that is processed when the mapping is triggered, will be linked

`to` method parameters:

 - action (required) -- new action name, user will be routed to this action instead of
     action declared originally in route method
 - controller (optional) -- new controller name. If not specified, controller,
     in which dispatcher is declared, will be linked

It is possible to pass a closure to a `to` method. This way the code encapsulated in the closure will be executed when the mapping is triggered:

```groovy
route(action: 'next') {
  map(action: 'success').to {
    // ...
    route action: 'saveUser'
  }
}
```

Dispatching concepts:

 * Dispatcher exists for a flow execution until the moment when one of its mappings is triggered.
 * Dispatchers are placed in a stack and are operated by FILO principle in case if more than one dispatcher is declared during the flow.
 * Only mappings of the last declared dispatcher are active and only these mappings can be dispatched.
 * Dispatcher is removed from the stack when one of its mapping is triggered, thus the mappings of the earlier added dispatcher becomes active.

Dispatching is a powerful mechanism which allows to build massive web flows that are based on reusable units. However, dispatching used wrongly or redundantly can lead to all sorts of serious navigation issues.

####Events

GDWF plugin designed to exactly fit Grails Web Flow plugin event triggering. This way the events can be triggered using standard Grails tags.

In example described below, when the user clicks a button on a rendered HTML page, he/she will trigger event "cancel" or the event "save", as they are the events specified in *name* attribute of *submitButton* element.

```html
<g:form>
   <g:submitButton name="cancel" value="Cancel"/>
   <g:submitButton name="save" value="Save"/>
</g:form>
```

Also it is possible to trigger an event specified in the *event* attribute of the *link* tag:

```html
<g:link event="next"/>
```

> Under the hood, when user clicks on the element which is rendered either by *submitButton* or *link* tag, event parameter (`_eventId_<event name>` for *submitButton* and `_eventId` for *link*) with event name as a value is passed along with the request.

####Scopes

The plugin adds `flowScope` which can be utilised to store objects within flow lifetime:

```groovy
flowScope.user = new User()
```

The objects stored within `flowScope` are accessible on *.gsp* pages just as if they are passed via *model* argument to a 'render' view method:

```html
<g:form>
   <p>Name: ${user?.name}</p>
   <g:textField bean="${user}" name="address"/>
</g:form>
```

> All default controller scopes are available to use: *servletContext*, *session*, *request*, *params* and *flash*.

####Serialization

Objects stored in the *flowScope* must implement *java.io.Serializable* interface:

```groovy
class User implements Serializable ...
```
```groovy
flowScope.user = new User()
```

as well as objects loaded into *Hibernate* session:

```groovy
@FlowState
def updateUser() {
  activity {
    def john = User.findByName(params.name)
    // ...
  }
}
```

####Data Binding

GDWF supports *CommandObject* to capture information submitted by the form, for instance:

```groovy
@FlowState
def saveUser() {
  save { User user ->
    if (!user.validate()) {
      return reset()
    }
    // ...
  }
}
```

however, it is possible to bind data from request parameters:

```groovy
@FlowState
def saveUser() {
  save {
    flowScope.user = new User(params)
    // or
    // flowScope.user = new User()
    // flowScope.user.properties = params
    // ...
  }
}
```

###Code Snippets

####Custom View

Glace Dynamic Web Flow

```groovy
@FlowState(view = '/user/details')
def setDetails() {
}
```

Spring Web Flow

```groovy
def createAccountFlow = {
  // ...
  setDetails {
    render view: '/user/details'
  }
}
```

####External redirect

Glace Dynamic Web Flow:

```groovy
@FlowState
def saveAccount() {
  activity {
    // ...
    redirect action: 'list', controller: 'user'
    // or even
    // route action: 'list', controller: 'user'
  }
}
```

Spring Web Flow:

```groovy
def createAccountFlow = {
  // ...
  saveAccount {
    action {
      // ...
    }
    on('success').to('end')
  }
  end {
    redirect action: 'list', controller: 'user'
  }
}
```

####Data Validation

Glace Dynamic Web Flow

```groovy
@FlowState
def setUserDetails() {
  submit { User user ->
    if (!user.validate()) {
      return reset()
    }
    // ...
  }
}

```

Spring Web Flow

```groovy
def createAccountFlow = {
  // ...
  setUserDetails {
    on('submit') { User user ->
      if (!user.validate()) {
        error()
      }
    }.to 'end'
  }
}
```

##IDE support

IntelliJ IDEA provides code completion and syntax highlighting of plugin's DSL.