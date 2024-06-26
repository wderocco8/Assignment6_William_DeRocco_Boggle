package com.example.assignment6_william_derocco_boggle

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), GameState.NewGameListener, Board.SubmitListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lock orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun onNewGameClicked() {
        // Get the Board fragment instance
        val boardFragment = supportFragmentManager.findFragmentById(R.id.board_fragment) as Board?

        // Call the startNewGame method of the Board fragment if it exists
        boardFragment?.newGame()
    }

    override fun onSubmitClicked(newScore: Int) {
        // Get the GameState fragment instance
        val gameStateFragment = supportFragmentManager.findFragmentById(R.id.game_state_fragment) as GameState?

        // Call the startNewGame method of the Board fragment if it exists
        gameStateFragment?.updateScore(newScore)
    }
}