package com.hyungilee.coroutineexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            // IO(Network, local database interaction),
            // Main(Main thread, interact with UI),
            // Default(heavy competition work)
            CoroutineScope(IO).launch {
                //launch (coroutine builder)
                fakeApiResult()
            }
        }
    }

    private fun setNewText(input: String){
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String){
        //CoroutineScope(Main)로 하거나
        withContext(Main){
            setNewText(input)
        }
    }

    private suspend fun fakeApiResult(){
        val result1 = getResult1FromApi()
        println("debug: $result1")
        setTextOnMainThread(result1)

        // result1 부분이 처리된 후에 result2부분이 순차적으로 실행될 것이다.
        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
        //text.setText(result1) //이 작업은 crash 될 것이다.
        // 그 이유는 background thread 에서 작업을 하고 있고,
        // 실제 UI와 interact 하고 있는 thread 는 Main thread 이기 때문이다.
    }

    //suspend - coroutine keyword
    // This function - Async method
    private suspend fun getResult1FromApi():String{
        logThread("getResult1FromApi")
        delay(1000) // sleep single coroutine
        //Thread.sleep(1000) // sleep all coroutine
        return RESULT_1
    }

    private suspend fun getResult2FromApi(): String{
        logThread("getResult2FromApi")
        delay(1000)
        return RESULT_2
    }

    //Coroutine != Thread
    private fun logThread(methodName: String){
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
