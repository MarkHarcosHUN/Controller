package web

import controller.*
import model.ControllerConfigurationModel

class WebApi(controller : Manageable) {

    val PORT = 1213
    val controller : Manageable

    init {
        this.controller = controller
    }

    fun startListening() {
        try {
            Thread.sleep(5000)
            //controller.start(ControllerConfigurationModel(arrayOf("")))
            controller.stop()
            controller.restart(ControllerConfigurationModel(arrayOf("")))
            controller.stop()
            controller.stop()
            controller.stop()
            controller.stop()



            controller.restart(ControllerConfigurationModel(arrayOf("")))
            Thread.sleep(5000)


            controller.restart(ControllerConfigurationModel(arrayOf("")))
        }catch(e : ControllerException){
            println(e.message)
        }
        Thread.sleep(10000)
        controller.start(ControllerConfigurationModel(arrayOf("")))
    }

}