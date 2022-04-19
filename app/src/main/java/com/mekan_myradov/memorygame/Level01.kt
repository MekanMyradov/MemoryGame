package com.mekan_myradov.memorygame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import com.mekan_myradov.memorygame.R.drawable.*
import kotlinx.android.synthetic.main.activity_main.*

class Level01 : AppCompatActivity() {
    var images: MutableList<Int> = mutableListOf(
        camel, coala, fox, lion,
        monkey, wolf, camel, coala,
        snake, lion, monkey, fox,
        snake, crocodile, crocodile, wolf
    )

    private  lateinit var cl: ConstraintLayout
    internal var score = 0
    private  lateinit var tvClicks: TextView
    private lateinit var  tvPairs: TextView
    internal lateinit var tvTimer: TextView

    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 30000
    internal val countDownInterval: Long = 1000

    var buttons: Array<ImageButton> = arrayOf()
    lateinit var cards: List<MemoryCard>
    var indexOfSelectedCard: Int? = null    // ? mark denotes nullable int
    var matchedPairs = 0
    var clicks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvClicks = findViewById(R.id.tvClicks)
        tvPairs = findViewById(R.id.tvPairs)
        cl = findViewById(R.id.cl)

        buttons = arrayOf(img01, img02, img03, img04, img05, img06, img07, img08, img09, img10, img11, img12, img13, img14, img15, img16)

        images.shuffle()    // shuffle the images before the launch of an app

        // Create a memory card for each image button
        cards = buttons.indices.map { index ->
            MemoryCard(images[index])
        }

        tvTimer = findViewById<TextView>(R.id.tvTimer)
        resetGame()

        if(!gameStarted){
            startGame()
        }
    }

    fun click(view: View){
        var whichButton: ImageButton = view as ImageButton
        var tag = whichButton.tag.toString().toInt()

        // Update Models
        updateModels(tag)

        // Flip the card
        flipCards()
    }

    private fun updateModels(tag: Int) {
        val card = cards[tag]

        // The card can not be pressed two times in a row
        if(card.isFaceUp){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            return
        }

        clicks++
        tvClicks.text = "Clicks: ${clicks}"

        /*
        // If all matches found
        if(matchedPairs == 8){
            Snackbar.make(cl, "You already won", Snackbar.LENGTH_LONG).show()
            return
        }
        */

        /*
        * There are 2 possible options:
        * (a) 0 or 2 cards are visible -> close unmatched cards and open the selected card
        * (b)        1 card is visible -> open the selected card and check if the cards match
        */

        if(indexOfSelectedCard == null){
            // (a) case

            closeUnmatchedCards()
            indexOfSelectedCard = tag
        }
        else{
            // (b) case
            checkForMatch(indexOfSelectedCard!!, tag)   // indexOfSelectedCard is a nullable variable so we put !! to force the Android Studio
            // closeUnmatchedCards()
            indexOfSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun closeUnmatchedCards() {
        for(card in cards){
            if(!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    private fun flipCards() {
        // If all matches found
        if(matchedPairs == 8){
            Snackbar.make(cl, "You win. Congratulations. Your score is ${score}", Snackbar.LENGTH_LONG).show()
            CommonConfetti.rainingConfetti(cl, intArrayOf(Color.GREEN, Color.YELLOW, Color.MAGENTA)).oneShot()  // confet
            return
        }

        cards.forEachIndexed{index, card ->
            val button = buttons[index]
            // if cards are matched then change the opacity
            if(card.isMatched){
                button.alpha = 0.5f
                // tvPairs.text = "Pairs: ${matchedPairs}/8"
            }
            if(card.isFaceUp) {
                button.setImageResource(images[index])
            }
            else{
                button.setImageResource(R.drawable.code)
            }
        }
    }

    private fun checkForMatch(indexOfSelectedCard: Int, tag: Int) {
        if (cards[indexOfSelectedCard].tag == cards[tag].tag){
            Toast.makeText(this, "Match Found", Toast.LENGTH_SHORT).show()
            cards[indexOfSelectedCard].isMatched = true
            cards[tag].isMatched = true
            matchedPairs++
            score += 10
            tvPairs.text = "Pairs: ${matchedPairs}/8"
        }else{
            score -= 2
        }
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, "Time is up! Your score is ${score}", Toast.LENGTH_LONG).show()
        //resetGame()
        val intent = Intent(this, LandingPage::class.java)
        intent.putExtra("Score", score)
        intent.putExtra("Level", 1)

        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_about){
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun resetGame() {
        score = 0
        val initialTimeLeft = initialCountDown / 1000
        tvTimer.text = "Time Left: ${initialTimeLeft}"

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                tvTimer.text = "Time Left: ${timeLeft}"
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }
}