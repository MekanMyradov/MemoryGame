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
import kotlinx.android.synthetic.main.activity_level02.*

class Level02 : AppCompatActivity() {

    private lateinit var cl02: ConstraintLayout
    internal var score02 = 0
    internal lateinit var tvClicks02: TextView
    internal lateinit var tvPairs02: TextView
    internal lateinit var tvTimer02: TextView

    internal var gameStarted02 = false
    internal lateinit var countDownTimer02: CountDownTimer
    internal val initialCountDown02: Long = 60000
    internal val countDownInterval02: Long = 1000

    var images: MutableList<Int> = mutableListOf(
        apple, aubergine, banana, cherry, cocumber, fig,
        apple, aubergine, banana, cherry, cocumber, fig,
        garlic, limon, melon, onion, orange, pear,
        garlic, limon, melon, onion, orange, pear,
        plum, pomegranate, potato, strawberry, tomato, watermelon,
        plum, pomegranate, potato, strawberry, tomato, watermelon
    )

    var buttons: Array<ImageButton> = arrayOf()

    lateinit var cards02: List<MemoryCard>
    var indexOfSelectedCard02: Int? = null    // ? mark denotes nullable int
    var matchedPairs02 = 0
    var clicks02 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level02)

        buttons = arrayOf(image01, image02, image03, image04, image05, image06, image07, image08, image09, image10, image11, image12,
                          image13, image14, image15, image16, image17, image18, image19, image20, image21, image22, image23, image24,
                          image25, image26, image27, image28, image29, image30, image31, image32, image33, image34, image35, image36)

        images.shuffle()

        // Create a memory card for each image button
        cards02 = buttons.indices.map { index ->
            MemoryCard(images[index])
        }

        cl02 = findViewById(R.id.cl02)
        tvClicks02 = findViewById<TextView>(R.id.tvClicks02)
        tvPairs02 = findViewById<TextView>(R.id.tvPairs02)
        tvTimer02 = findViewById<TextView>(R.id.tvTimer02)
        resetGame()

        if(!gameStarted02){
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
        val card = cards02[tag]

        // The card can not be pressed two times in a row
        if(card.isFaceUp){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
            return
        }

        clicks02++
        tvClicks02.text = "Clicks: ${clicks02}"

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

        if(indexOfSelectedCard02 == null){
            // (a) case

            closeUnmatchedCards()
            indexOfSelectedCard02 = tag
        }
        else{
            // (b) case
            checkForMatch(indexOfSelectedCard02!!, tag)   // indexOfSelectedCard is a nullable variable so we put !! to force the Android Studio
            // closeUnmatchedCards()
            indexOfSelectedCard02 = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun closeUnmatchedCards() {
        for(card in cards02){
            if(!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    private fun flipCards() {
        // If all matches found
        if(matchedPairs02 == 18){
            Snackbar.make(cl02, "You win. Congratulations. Your score is ${score02}", Snackbar.LENGTH_LONG).show()
            CommonConfetti.rainingConfetti(cl02, intArrayOf(Color.GREEN, Color.YELLOW, Color.MAGENTA)).oneShot()  // confet
            return
        }

        cards02.forEachIndexed{index, card ->
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

    private fun checkForMatch(indexOfSelectedCard02: Int, tag: Int) {
        if (cards02[indexOfSelectedCard02].tag == cards02[tag].tag){
            Toast.makeText(this, "Match Found", Toast.LENGTH_SHORT).show()
            cards02[indexOfSelectedCard02].isMatched = true
            cards02[tag].isMatched = true
            matchedPairs02++
            score02 += 10
            tvPairs02.text = "Pairs: ${matchedPairs02}/18"
        }else{
            score02 -= 2
        }
        //tvScore.text = "Score: ${score}"
    }

    private fun startGame(){
        countDownTimer02.start()
        gameStarted02 = true
    }

    private fun endGame(){
        Toast.makeText(this, "Time is up! Your score is ${score02}", Toast.LENGTH_LONG).show()
        //resetGame()
        val intent = Intent(this, LandingPage::class.java)
        intent.putExtra("Score", score02)
        intent.putExtra("Level", 2)

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

    private fun resetGame(){
        score02 = 0
        val initialTimeLeft = initialCountDown02 / 1000
        tvTimer02.text = "Time Left: ${initialTimeLeft}"

        countDownTimer02 = object: CountDownTimer(initialCountDown02, countDownInterval02){
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                tvTimer02.text = "Time Left: ${timeLeft}"
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted02 = false
    }
}