package com.sysgears.gdwf.persistence

interface IFlowExecutionListener {
    void open()
    void pause()
    void resume()
    void close()
    void discard()
}