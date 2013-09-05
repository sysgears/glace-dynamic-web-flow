import com.sysgears.gdwf.FlowStage
import com.sysgears.gdwf.execution.ExecutionScope
import com.sysgears.gdwf.execution.ExecutorDescriptor
import com.sysgears.gdwf.execution.ExecutorFetcher
import com.sysgears.gdwf.execution.item.executors.ActivityScopeItemExecutor
import com.sysgears.gdwf.execution.item.executors.CallbackScopeItemExecutor
import com.sysgears.gdwf.execution.item.executors.EventScopeItemExecutor
import com.sysgears.gdwf.execution.item.ItemExecutorFactory
import com.sysgears.gdwf.execution.item.executors.SetupActivityScopeItemExecutor
import com.sysgears.gdwf.execution.recall.executors.ActivityScopeRecallExecutor
import com.sysgears.gdwf.execution.recall.executors.CallbackScopeRecallExecutor
import com.sysgears.gdwf.execution.recall.executors.EventScopeRecallExecutor
import com.sysgears.gdwf.execution.recall.RecallExecutorFactory
import com.sysgears.gdwf.execution.recall.executors.SetupActivityScopeRecallExecutor
import com.sysgears.gdwf.execution.render.executors.ActivityScopeRenderExecutor
import com.sysgears.gdwf.execution.render.executors.CallbackScopeRenderExecutor
import com.sysgears.gdwf.execution.render.executors.EventScopeRenderExecutor
import com.sysgears.gdwf.execution.render.RenderExecutorFactory
import com.sysgears.gdwf.execution.render.executors.SetupActivityScopeRenderExecutor
import com.sysgears.gdwf.execution.route.executors.ActivityScopeRouteExecutor
import com.sysgears.gdwf.execution.route.executors.CallbackScopeRouteExecutor
import com.sysgears.gdwf.execution.route.executors.EventScopeRouteExecutor
import com.sysgears.gdwf.execution.route.RouteExecutorFactory
import com.sysgears.gdwf.execution.route.executors.SetupActivityScopeRouteExecutor
import com.sysgears.gdwf.mixins.GDWFMethods
import com.sysgears.gdwf.mixins.GDWFMixin
import com.sysgears.gdwf.mixins.GDWFMixinStub
import com.sysgears.gdwf.persistence.HibernateSessionHolder
import com.sysgears.gdwf.persistence.HibernateFlowExecutionListener
import com.sysgears.gdwf.persistence.PersistenceScopeLayerTag
import com.sysgears.gdwf.persistence.FlowExecutionListeners
import com.sysgears.gdwf.registry.Events
import com.sysgears.gdwf.registry.FlowRegistry
import com.sysgears.gdwf.repository.EntryRepository
import com.sysgears.gdwf.repository.EntryRepositoryScopeLayerTag
import com.sysgears.gdwf.scope.FlowScope
import com.sysgears.gdwf.scope.FlowScopeLayerTag
import com.sysgears.gdwf.execution.ExecutionScopeProxy
import com.sysgears.gdwf.storage.EmptyScopeLayerTag
import com.sysgears.gdwf.storage.ServletScopeAccessor
import com.sysgears.gdwf.storage.SessionScopeAccessor
import com.sysgears.gdwf.storage.Storage
import com.sysgears.gdwf.token.TokenGenerator
import com.sysgears.gdwf.token.TokenProxy
import com.sysgears.gdwf.token.accessors.FlowTokenAccessor
import com.sysgears.gdwf.token.holders.ExecutionTokenHolder
import com.sysgears.gdwf.token.holders.ExecutionTokenScopeLayerTag
import com.sysgears.gdwf.token.holders.SnapshotTokenHolder
import com.sysgears.gdwf.token.holders.SnapshotTokenScopeLayerTag
import grails.web.Action

import java.lang.reflect.Method

class GlaceDynamicWebFlowGrailsPlugin {

    def version = '0.1.0'
    def grailsVersion = '2.0 > *'

    def title = 'Glace Web Flow Plugin'
    def description = """Glace Dynamic Web Flow plugin is developed to make Grails implementation of a web flow
flexible and dynamic. The plugin inherits Spring Web Flow paradigms and utilizes principle of decentralised
configuration. This approach allows to override sequence of flow steps and affect transition rules at runtime."""

    def author = 'SysGears, LLC'
    def authorEmail = 'info@sysgears.com'
    def developers = [[name: 'Dmitriy Pavlenko', email: 'dmitriy.pavlenko@sysgears.com'],
            [name: 'Andrey Shevchenko', email: 'andrey.shevchenko@sysgears.com']]
    def organization = [name: "SysGears LLC", url: "http://sysgears.com"]

    def documentation = 'https://github.com/sysgears/glace-dynamic-web-flow/blob/master/README.md'
    def license = 'APACHE'
    def issueManagement = [system: 'github', url: 'https://github.com/sysgears/glace-dynamic-web-flow/issues']
    def scm = [url: 'https://github.com/sysgears/glace-dynamic-web-flow']

    List watchedResources = ['file:./grails-app/controllers/**/*Controller.groovy',
            'file:./plugins/*/grails-app/controllers/**/*Controller.groovy']

    List loadAfter = ['controllers']

    def doWithSpring = {

        // inner bean, allows to access servlet scope
        def servletScopeAccessor = { ServletScopeAccessor ssa -> }

        // inner bean, allows to access session scope
        def sessionScopeAccessor = { SessionScopeAccessor ssa -> }

        // inner bean, keeps flow execution entries in session scope
        def entryStorage = { Storage repositoryStorage ->
            tag = { EntryRepositoryScopeLayerTag repositoryTag ->
                tokenProxy = gdwfTokenProxy
            }
            scopeAccessor = sessionScopeAccessor
        }

        // proxy to get token value
        gdwfTokenProxy(TokenProxy)

        // flow scope to store objects
        gdwfScope(FlowScope) {
            storage = { Storage scopeStorage ->
                tag = { FlowScopeLayerTag scopeTag ->
                    tokenProxy = gdwfTokenProxy
                }
                scopeAccessor = sessionScopeAccessor
            }
        }

        // repository to keep execution entries in session scope
        gdwfEntryRepository(EntryRepository) {
            storage = entryStorage
            tokenProxy = gdwfTokenProxy
        }

        // registry which uses servlet context to store all the application flows
        gdwfRegistry(FlowRegistry) {
            storage = { Storage flowRegistryStorage ->
                tag = { EmptyScopeLayerTag flowRegistryTag -> }
                scopeAccessor = servletScopeAccessor
            }
        }

        // token generator to create new token value for every execution
        gdwfTokenGenerator(TokenGenerator) {

            def flowTA = { FlowTokenAccessor sta ->
                flowRegistry = gdwfRegistry
            }
            def executionTH = { ExecutionTokenHolder executionTokenHolder ->
                storage = { Storage executionTokenStorage ->
                    tag = { ExecutionTokenScopeLayerTag executionTokenTag -> }
                    scopeAccessor = sessionScopeAccessor
                }
            }
            def snapshotTH = { SnapshotTokenHolder sth ->
                storage = { Storage snapshotTokenStorage ->
                    tag = { SnapshotTokenScopeLayerTag snapshotTokenTag ->
                        executionTokenHolder = executionTH
                        flowTokenAccessor = flowTA
                    }
                    scopeAccessor = sessionScopeAccessor
                }
            }
            def executionAwareSnapshotTH = { SnapshotTokenHolder sth ->
                storage = entryStorage
            }

            executionTokenHolder = executionTH
            snapshotTokenHolder = snapshotTH
            executionAwareSnapshotTokenHolder = executionAwareSnapshotTH
            flowTokenAccessor = flowTA
            executionAwareFlowTokenAccessor = gdwfTokenProxy
        }

        // inner bean, allows to access cached hibernate session
        def hibernateSessionStorage = { Storage s ->
            tag = { PersistenceScopeLayerTag persistenceTag ->
                tokenProxy = gdwfTokenProxy
            }
            scopeAccessor = sessionScopeAccessor
        }

        // inner bean, holds flow hibernate session
        def flowScopeSessionHolder = { HibernateSessionHolder hibernateSH ->
            storage = hibernateSessionStorage
            key = 'gdwfHibernateSession'
        }

        // hibernate listener to manipulate hibernate session, allows to implement session per conversation pattern
        gdwfHibernateFlowExecutionListener(HibernateFlowExecutionListener) {
            sessionFactory = ref('sessionFactory')
            transactionManager = ref('transactionManager')
            requestSessionHolder = { HibernateSessionHolder requestSH ->
                storage = hibernateSessionStorage
                key = 'requestHibernateSession'
            }
            flowSessionHolder = flowScopeSessionHolder
        }

        gdwfFlowExecutionListeners(FlowExecutionListeners) {
            listeners = [gdwfHibernateFlowExecutionListener]
        }

        gdwfExecutionScopeProxy(ExecutionScopeProxy)

        def executorFetcherBean = { ExecutorFetcher ef ->
            executionScopeProxy = gdwfExecutionScopeProxy
        }

        gdwfItemExecutorFactory(ItemExecutorFactory) {
            tokenProxy = gdwfTokenProxy
            entryRepository = gdwfEntryRepository
            scope = gdwfScope
            flowSessionHolder = flowScopeSessionHolder
            tokenGenerator = gdwfTokenGenerator
            flowExecutionListeners = gdwfFlowExecutionListeners
            webFlowMethodsClass = GDWFMethods.class
            executorFetcher = executorFetcherBean
            executorDescriptors = [
                    new ExecutorDescriptor(ExecutionScope.ACTIVITY_EXECUTION_SCOPE, ActivityScopeItemExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.CALLBACK_EXECUTION_SCOPE, CallbackScopeItemExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.EVENTS_EXECUTION_SCOPE, EventScopeItemExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.SETUP_ACTIVITY_EXECUTION_SCOPE, SetupActivityScopeItemExecutor.class)
            ]
        }

        gdwfRecallExecutorFactory(RecallExecutorFactory) {
            entryRepository = gdwfEntryRepository
            scope = gdwfScope
            tokenProxy = gdwfTokenProxy
            flowSessionHolder = flowScopeSessionHolder
            executorFetcher = executorFetcherBean
            executorDescriptors = [
                    new ExecutorDescriptor(ExecutionScope.ACTIVITY_EXECUTION_SCOPE, ActivityScopeRecallExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.CALLBACK_EXECUTION_SCOPE, CallbackScopeRecallExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.EVENTS_EXECUTION_SCOPE, EventScopeRecallExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.SETUP_ACTIVITY_EXECUTION_SCOPE, SetupActivityScopeRecallExecutor.class)
            ]
        }

        gdwfRenderExecutorFactory(RenderExecutorFactory) {
            entryRepository = gdwfEntryRepository
            scope = gdwfScope
            flowSessionHolder = flowScopeSessionHolder
            executorFetcher = executorFetcherBean
            executorDescriptors = [
                    new ExecutorDescriptor(ExecutionScope.ACTIVITY_EXECUTION_SCOPE, ActivityScopeRenderExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.CALLBACK_EXECUTION_SCOPE, CallbackScopeRenderExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.EVENTS_EXECUTION_SCOPE, EventScopeRenderExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.SETUP_ACTIVITY_EXECUTION_SCOPE, SetupActivityScopeRenderExecutor.class)
            ]
        }

        gdwfRouteExecutorFactory(RouteExecutorFactory) {
            tokenProxy = gdwfTokenProxy
            entryRepository = gdwfEntryRepository
            scope = gdwfScope
            flowSessionHolder = flowScopeSessionHolder
            tokenGenerator = gdwfTokenGenerator
            flowRegistry = gdwfRegistry
            flowExecutionListeners = gdwfFlowExecutionListeners
            executorFetcher = executorFetcherBean
            executorDescriptors = [
                    new ExecutorDescriptor(ExecutionScope.ACTIVITY_EXECUTION_SCOPE, ActivityScopeRouteExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.CALLBACK_EXECUTION_SCOPE, CallbackScopeRouteExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.EVENTS_EXECUTION_SCOPE, EventScopeRouteExecutor.class),
                    new ExecutorDescriptor(ExecutionScope.SETUP_ACTIVITY_EXECUTION_SCOPE, SetupActivityScopeRouteExecutor.class)
            ]
        }
    }

    def doWithApplicationContext = { applicationContext ->
        registerFlows(application, applicationContext)
    }

    def onChange = { event ->
        registerFlows(event.application, event.ctx, event.source)
    }

    private static void registerFlows(application, ctx, source = null) {
        // get flow registry
        def registry = ctx.getBean('gdwfRegistry') as FlowRegistry
        registry.storage.scopeAccessor.applicationContext = ctx

        // remove all of the flows from the registry
        registry.clear()

        // register flow states and add event handlers to cache
        def register = { controller, actions, findStage, callAction ->
            def controllerInstance = null
            actions.each { action ->
                // find out whether the action is flow state
                FlowStage stage = findStage(action)
                if (stage) {
                    // create new class instance and mix the instance with controller mixin stub
                    controllerInstance = controllerInstance ?: createMixedControllerInstance(controller.clazz, GDWFMixinStub)
                    // obtain event handlers
                    Events events = callAction(controllerInstance, "$action.name") as Events
                    // cache state
                    registry.registerGDWFItem(controller.logicalPropertyName, action.name,
                            stage == FlowStage.FLOW_SETUP ? null : action.getAnnotation(stage.annotationType).view(),
                            stage, events)
                }
            }
            // return true if controller contains flow states
            controllerInstance != null
        }

        def callClosureAction = { controller, name ->
            controller."$name".doCall()
        }

        def callMethodAction = { controller, name ->
            Method method = controller.class.declaredMethods.find { it.name == name }
            method.invoke(controller, new Object[method.genericParameterTypes.size()])
        }

        def findStageForClosure = { action ->
            FlowStage.values().find { action.isAnnotationPresent(it.annotationType) }
        }

        def findStageForMethod = { action ->
            FlowStage.values().find { action.isAnnotationPresent(it.annotationType) && action.isAnnotationPresent(Action.class) }
        }

        def sourceControllerArtefact = source ? application.getControllerClass(source.getName()) : null

        for (controller in application.controllerClasses) {
            if ((register(controller, controller.clazz.declaredFields, findStageForClosure, callClosureAction) |
                    register(controller, controller.clazz.methods, findStageForMethod, callMethodAction)) &&
                    (!source || sourceControllerArtefact == controller)) {
                if (!controller.metaClass.respondsTo(controller.clazz.newInstance(), 'gdwfCachedRedirect', Map)) {
                    def originalRedirectMethod = controller.metaClass.getMetaMethod('redirect', [Map] as Class[])
                    controller.metaClass.gdwfCachedRedirect = { Map args ->
                        originalRedirectMethod.invoke(delegate, args)
                    }
                    def originalRenderMethod = controller.metaClass.getMetaMethod('render', [Map] as Class[])
                    controller.metaClass.gdwfCachedRender = { Map args ->
                        originalRenderMethod.invoke(delegate, args)
                    }
                }

                controller.clazz.mixin GDWFMixin
            }
        }
    }

    private static def createMixedControllerInstance(Class controller, Class mixin) {
        def controllerInstance = controller.newInstance()
        controllerInstance.metaClass.mixin mixin
        controllerInstance
    }
}
