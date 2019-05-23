package com.aleksejantonov.tajikair.sl

object SL {
  private lateinit var componentManager: ComponentManager

  fun init() {
    componentManager = ComponentManager()
  }

  fun componentManager() = componentManager
}