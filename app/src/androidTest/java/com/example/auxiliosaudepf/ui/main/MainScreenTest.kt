package com.example.auxiliosaudepf.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.example.auxiliosaudepf.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { MainScreen(onItemClick = {}) }
  }

  @Test
  fun appTitle_exists() {
    composeTestRule.onNodeWithText("Auxílio Saúde").assertExists()
    composeTestRule.onNodeWithText("Polícia Federal").assertExists()
  }

  @Test
  fun buttons_exist() {
    composeTestRule.onNodeWithText("Tirar Foto").assertExists()
    composeTestRule.onNodeWithText("Ver Comprovantes").assertExists()
    composeTestRule.onNodeWithText("Upload Comprovantes").assertExists()
    composeTestRule.onNodeWithText("Relatório Ressarcimento").assertExists()
  }
}
