package controller

import model.ControllerConfigurationModel
import model.GatewayConfigurationModel
import web.WebApi
import kotlin.concurrent.thread

class Controller() : Manageable {

    lateinit var controllerConfigurationModel: ControllerConfigurationModel
    private var controllerState : ControllerState
    val webApi: WebApi
    var testValue = 1

    init {
        controllerState=ControllerState.NOT_RUNNING
        webApi = WebApi(this).also {
            thread(start = true) {
                it.startListening()
            }
        }
    }

    override fun start(controllerConfigurationModel: ControllerConfigurationModel) {
        if (controllerState != ControllerState.NOT_RUNNING) throw ControllerException("Cant start: "+controllerState)

        thread(start = true) {
            setup(controllerConfigurationModel)
            startSupervision()
            cleanup()
        }
    }

    override fun stop() {
        if (controllerState != ControllerState.RUNNING) throw ControllerException("Cant stop: "+controllerState)

        println("stopping gateway...")
        controllerState = ControllerState.TERMINATING
    }

    override fun restart(controllerConfigurationModel: ControllerConfigurationModel) {
        if (controllerState != ControllerState.RUNNING) throw ControllerException("Cant restart: "+controllerState)
        thread(start=true){
            print("Restarting ...")
            stop()
            while (controllerState != ControllerState.NOT_RUNNING) {
                println(" waiting for modules to stop")
                Thread.sleep(1000)
            }
            // thread a threadben..lehet memory leak
            start(controllerConfigurationModel)
        }

    }

    private fun cleanup() {
        println("stopping gateway modules...")
        stopModules()
        controllerState = ControllerState.NOT_RUNNING
        println("Gateway stopped")
    }

    private fun setup(controllerConfigurationModel: ControllerConfigurationModel) {
        controllerState = ControllerState.INITIALIZING
        this.controllerConfigurationModel = controllerConfigurationModel
        var gatewayConfigurationModel = fillModelFromDatabase()
        startModules(gatewayConfigurationModel)
    }

    private fun startModules(gatewayConfigurationModel: GatewayConfigurationModel) {
        println("Starting modules")
    }

    private fun startSupervision() {
        println("Supervision started")
        controllerState = ControllerState.RUNNING
        while (controllerState == ControllerState.RUNNING) {
            println("testvalue: ${testValue++}")
            Thread.sleep(1000)
        }
    }

    private fun fillModelFromDatabase(): GatewayConfigurationModel {
        println("Reading configuration database")
        return GatewayConfigurationModel()
    }

    private fun createControllerConfigurationModel(): ControllerConfigurationModel {
        return ControllerConfigurationModel(arrayOf(""))
    }


    private fun stopModules() {
        Thread.sleep(3000)
    }


}

interface Manageable {

    fun start(controllerConfigurationModel: ControllerConfigurationModel)
    fun stop()
    fun restart(controllerConfigurationModel: ControllerConfigurationModel)
}

fun main() {
    Controller()
}

enum class ControllerState {
    NOT_RUNNING, RUNNING, INITIALIZING, TERMINATING
}