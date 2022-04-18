package com.example.juegotresenraya_recuperacion

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.get
import com.example.juegotresenraya_recuperacion.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.properties.Delegates.observable



const val player1Token = "X"
const val player2Token = "O"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private val tableState = Array(9) { _ -> "" }

  private var playerTurn: Int by observable(1) { property, oldValue, newValue ->
    this.changeTurnMessage(newValue)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    this.binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(this.binding.root)


    this.changeTurnMessage(this.playerTurn)

    // Bind click listener for all the cells
    this.binding.table.forEach { view ->
      view.setOnClickListener {
        val index = this.binding.table.indexOfChild(view)
        if (this.tableState[index] != "") return@setOnClickListener

        this.setCell(index)

        val winner = this.getWinner()
        if (winner != -1) {
          this.alert("Player ${this.playerTurn} wins!!") { this.resetGame() }
          return@setOnClickListener
        }

        if (this.isTableFull()) {
          this.alert("It's a tie!") { this.resetGame() }
          return@setOnClickListener
        }

        this.toggleTurn()
      }
    }
  }

  // Utility functions for the game

  private fun alert(message: String, callback: () -> Unit = { }) {
    MaterialAlertDialogBuilder(this)
    .setMessage(message)
    .setPositiveButton("OK") { _, _ -> callback() }
    .setOnCancelListener() { callback() }
    .show()

  }

  private fun setCell(index: Int) {
    var token = player1Token
    if (this.playerTurn == 2) token = player2Token

    this.tableState[index] = token
    this.getCellView(index).text = token
  }

  private fun getCellView(index: Int): TextView {
    val cell = this.binding.table[index] as TextView
    return cell
  }

  private fun toggleTurn() {
    if (this.playerTurn == 2) this.playerTurn = 1
    else                      this.playerTurn = 2
  }

  private fun changeTurnMessage(turn: Int) {
    this.binding.playerTurnMessage.text = "Player ${turn}'s turn"
  }

  private fun isTableFull(): Boolean {
    val table = this.tableState
    for (token in table) {
      if (token == "") return false
    }

    return true
  }

  private fun resetGame() {
    this.playerTurn = 1
    this.changeTurnMessage(this.playerTurn)
    this.tableState.fill("")
    this.binding.table.forEach { view ->
      val textView = view as TextView
      textView.text = ""
    }
  }

  private fun getPlayerFromToken(token: String): Int {
    if (token == player1Token) return 1
    if (token == player2Token) return 2
    return -1
  }

  // Return  1 if winner is Player 1
  // Return  2 if winner is Player 2
  // Return -1 if there is no current winner
  private fun getWinner(): Int {
    val t = this.tableState

    // Horizontal Cases

    // X X X
    // - - -
    // - - -
    if (t[0] != "" && t[0] == t[1] && t[1] == t[2]) {
      return this.getPlayerFromToken(t[0])
    }

    // - - -
    // X X X
    // - - -
    if (t[0 + 3] != "" && t[0 + 3] == t[1 + 3] && t[1 + 3] == t[2 + 3]) {
      return this.getPlayerFromToken(t[0 + 3])
    }

    // - - -
    // - - -
    // X X X
    if (t[0 + 3*2] != "" && t[0 + 3*2] == t[1 + 3*2] && t[1 + 3*2] == t[2 + 3*2]) {
      return this.getPlayerFromToken(t[0 + 3*2])
    }


    // Vertical Cases

    // X - -
    // X - -
    // X - -
    if (t[0] != "" && t[0] == t[3] && t[3] == t[6]) {
      return this.getPlayerFromToken(t[0])
    }

    // - X -
    // - X -
    // - X -
    if (t[1] != "" && t[1] == t[1 + 3] && t[1 + 3] == t[1 + 6]) {
      return this.getPlayerFromToken(t[1])
    }

    // - - X
    // - - X
    // - - X
    if (t[2] != "" && t[2] == t[2 + 3] && t[2 + 3] == t[2 + 6]) {
      return this.getPlayerFromToken(t[2])
    }


    // Diagonal Cases

    // X - -
    // - X -
    // - - X
    if (t[0] != "" && t[0] == t[4] && t[4] == t[8]) {
      return this.getPlayerFromToken(t[0])
    }

    // - - X
    // - X -
    // X - -
    if (t[2] != "" && t[2] == t[4] && t[4] == t[6]) {
      return this.getPlayerFromToken(t[2])
    }

    return -1
  }
}
