package com.example.cw2

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class SearchForMeals : AppCompatActivity() {

    // class variables
    private lateinit var searchTxt: TextInputEditText
    private var mealsByNames : ArrayList<String> = arrayListOf()
    private lateinit var output : TextView
    private lateinit var scrollView : ScrollView
    private lateinit var image : ImageView
    private var allMealsDetails = java.lang.StringBuilder()
    private var allMeal : ArrayList<Meal> = arrayListOf()
    private var ingredients : ArrayList<Ingredients> = arrayListOf()
    private var measurements : ArrayList<Measurements> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_meals)

        output = findViewById(R.id.mealDetails)
        scrollView = findViewById(R.id.scrollView)
        searchTxt = findViewById(R.id.searchField)
        image = findViewById(R.id.image)

        val search = findViewById<Button>(R.id.search)
        val searchWeb = findViewById<Button>(R.id.searchFromWeb)

        search.setOnClickListener {
            search()
        }

        searchWeb.setOnClickListener {
            searchFromWebService()
        }

    }

    // when user click on the search button run this function
    private fun search() {
        val txt = searchTxt.text.toString()  // convert to a string
        // check empty or not
        if (txt.isNotEmpty()) {
            val db = Room.databaseBuilder(this, AppDatabase::class.java, "MealPlan.db").build()
            val mealDao = db.mealDao()
            val ingredientsDao = db.ingredientsDao()

            // search and get output from sqlite database
            runBlocking {
                launch {
                    // search whether any meal name contains the input text in the database
                    val meal = mealDao.getMealByNames("%$txt%")
                    for (i in 0..meal.size-1) {
                        mealsByNames.add(meal[i])            // get the meal name and add it to the list
                        println(meal[i])
                    }

                    // search whether any ingredient contains the input text in the database
                    val meal1 = ingredientsDao.getMealByIngredients("%$txt%")
                    for (i in 0..meal1.size - 1) {
                        if (meal1[i] !in mealsByNames) {    // check whether the meal is already in the list, if not add it
                            mealsByNames.add(meal1[i])
                            println(meal1[i])
                        }
                    }
                    // run this method
                    showOutput()

                }
            }
            // if there is at least one meal, show the details of the meal
            if (allMealsDetails.isNotEmpty()) {
                scrollView.setBackgroundColor(resources.getColor(R.color.white))  // set background colour
                output.text = allMealsDetails
                allMealsDetails.clear()
                mealsByNames.clear()
            }

        }
        // if user click search button without typing anything, show this msg
        else {
            Toast.makeText(applicationContext, "Enter Meal Name or Ingredient", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showOutput(){
        // check array is empty or not
        if (mealsByNames.isNotEmpty()) {

            val db = Room.databaseBuilder(this, AppDatabase::class.java, "MealPlan.db").build()
            val mealDao = db.mealDao()
            val ingredientsDao = db.ingredientsDao()
            val measurementsDao = db.measurementsDao()

            runBlocking {
                launch {
                    println(mealsByNames.size)
                    for (i in 0 until mealsByNames.size) {

                        val m = mealDao.getMealDetails(mealsByNames[i])
                        val ing = ingredientsDao.getIngredientsDetails(mealsByNames[i])
                        val me = measurementsDao.getMeasurementsDetails(mealsByNames[i])

                        allMealsDetails.append("Meal : ${m[0].mealName} \n")
                        allMealsDetails.append("DrinkAlternate : ${m[0].drink} \n")
                        allMealsDetails.append("Category : ${m[0].category} \n")
                        allMealsDetails.append("Area : ${m[0].area} \n")
                        allMealsDetails.append("Instructions : ${m[0].instructions} \n")
                        allMealsDetails.append("MealThumb : ${m[0].mealThumb} \n")
                        allMealsDetails.append("Tags : ${m[0].tags} \n")
                        allMealsDetails.append("Youtube : ${m[0].youtube} \n")

                        // load the image url and display it
                        runOnUiThread {
                            Picasso.get().load(m[0].mealThumb).into(image)
                        }

                        val split = ing.split(",")
                        for (j in 0..split.size - 1) {
                            if (j == 0) {
                                allMealsDetails.append("Ingredient" + (j + 1) + ":" + split[j].removePrefix("[") + "\n")
                            } else if (j == split.size - 1) {
                                allMealsDetails.append("Ingredient" + (j + 1) + ":" + split[j].removeSuffix("]") + "\n")
                            } else {
                                allMealsDetails.append("Ingredient" + (j + 1) + ":" + split[j] + "\n")
                            }
                        }

                        val split1 = me.split(",")
                        for (k in 0..split1.size - 1) {
                            if (k == 0) {
                                allMealsDetails.append("Measure" + (k + 1) + ":" + split1[k].removePrefix("[") + "\n")
                            } else if (k == split1.size - 1) {
                                allMealsDetails.append("Measure" + (k + 1) + ":" + split1[k].removeSuffix("]") + "\n")
                            } else {
                                allMealsDetails.append("Measure" + (k + 1) + ":" + split1[k] + "\n")
                            }
                        }

                        allMealsDetails.append("Source : ${m[0].source} \n")
                        allMealsDetails.append("ImageSource : ${m[0].imgSource} \n")
                        allMealsDetails.append("CreativeCommonsConfirmed : ${m[0].CreativeCommonsConfirmed} \n")
                        allMealsDetails.append("dateModified : ${m[0].dateModified} \n")
                        allMealsDetails.append("-----------------------------------------------------------\n\n")

                        // create objects for Meal, Ingredient, Measure and add them to a list
                        val meals = Meal(m[0].mealName,m[0].drink,m[0].category,m[0].area,m[0].instructions,m[0].mealThumb,m[0].tags,m[0].youtube,m[0].source,m[0].imgSource,m[0].CreativeCommonsConfirmed,m[0].dateModified)
                        allMeal.add(meals)
                        val ingr = Ingredients(m[0].mealName,split.toString())
                        ingredients.add(ingr)
                        val measure = Measurements(m[0].mealName,split1.toString())
                        measurements.add(measure)

                    }

                }
            }

        }

    }

    // method to run if user clicks on receive from web service button
    private fun searchFromWebService(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Search Meals from Web Service")
        builder.setMessage("Enter Meal Name")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            // run these codes when the OK button is clicked
            val txt = input.text.toString()         // convert to a string
            if (txt.isEmpty()) {                   // check input is empty or not
                val toast = Toast.makeText(applicationContext, "Type Meal Name", Toast.LENGTH_LONG)
                toast.show()
            } else {                // if input is not empty
                getFullDetails(txt)
                scrollView.setBackgroundColor(resources.getColor(R.color.white))
                output.text = allMealsDetails
                allMealsDetails.clear()
            }

        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            input.setText("")
        }
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun getFullDetails(name : String){
        val stb = StringBuilder()                   // create StringBuilder instance
        val urlString = "https://www.themealdb.com/api/json/v1/1/search.php?s=$name"    // url to search by meal name
        val url = URL(urlString)                // create a URL object
        val con : HttpURLConnection = url.openConnection() as HttpURLConnection        // opens a connection to that URL
        runBlocking {
            launch {
                // run the code of the coroutine in a new thread
                withContext(Dispatchers.IO) {
                    val bf = BufferedReader(InputStreamReader(con.inputStream))    //read the response data from the server's input stream.
                    var line: String? = bf.readLine()
                    while (line != null) {              //  read the data line by line
                        stb.append(line + "\n")
                        line = bf.readLine()
                    }
                    parseJSON1(stb)
                }
            }
        }

    }

    private fun parseJSON1(stb: java.lang.StringBuilder) {
        // this contains the full JSON returned by the Web Service
        val json = JSONObject(stb.toString())
        // Information about all the meals extracted by this function
        val jsonArray: JSONArray = json.getJSONArray("meals")
        // extract all the meals from the JSON array
        for (i in 0..jsonArray.length()-1) {
            val meal = jsonArray.getJSONObject(i) // this is a json object
            // get the details of the meal
            val mealName = meal.getString("strMeal")
            val drink = meal.getString("strDrinkAlternate")
            val category = meal.getString("strCategory")
            val area = meal.getString("strArea")
            val instruction = meal.getString("strInstructions")
            val mealThumb = meal.getString("strMealThumb")
            val tags = meal.getString("strTags")
            val youtube = meal.getString("strYoutube")
            val source = meal.getString("strSource")
            val imgSource =meal.getString("strImageSource")
            val creativeCommons = meal.getString("strCreativeCommonsConfirmed")
            val dateModified = meal.getString("dateModified")
            val ingredientsList = arrayListOf<String>()
            val measurementsList = arrayListOf<String>()

            // load the image url and display it
            runOnUiThread {
                Picasso.get().load(mealThumb).into(image)
            }

            // get ingredients and if the value is not empty and null then add to the arraylist
            for (i in 1..20){
                val ingredient = meal.getString("strIngredient$i")
                val ingStr = ingredient.toString()
                if (ingStr != "" && ingStr != "null") {
                    ingredientsList.add(ingStr)
                }
            }

            // get measurements and if the value is not empty and null then add to the arraylist
            for (i in 1..20){
                val measure = meal.getString("strMeasure$i")
                val measureStr = measure.toString()
                if (measureStr != "" && measureStr != "null") {
                    measurementsList.add(measureStr)
                }
            }

            // append meal details to a string builder
            allMealsDetails.append("Meal : $mealName \n")
            allMealsDetails.append("DrinkAlternate : $drink \n")
            allMealsDetails.append("Category : $category \n")
            allMealsDetails.append("Area : $area \n")
            allMealsDetails.append("Instructions : $instruction \n")
            allMealsDetails.append("MealThumb : $mealThumb \n")
            allMealsDetails.append("Tags : $tags \n")
            allMealsDetails.append("Youtube : $youtube \n")

            for (i in 0..ingredientsList.size-1){
                allMealsDetails.append("Ingredient"+(i+1)+":" + ingredientsList[i] + "\n")
            }
            for (i in 0..measurementsList.size-1){
                allMealsDetails.append("Measure"+(i+1)+":" + measurementsList[i] + "\n")
            }
            allMealsDetails.append("Source : $source \n")
            allMealsDetails.append("ImageSource : $imgSource \n")
            allMealsDetails.append("CreativeCommonsConfirmed : $creativeCommons \n")
            allMealsDetails.append("dateModified : $dateModified \n")
            allMealsDetails.append("-----------------------------------------------------------\n\n")

            // create objects for Meal, Ingredient, Measure and add them to a list
            val meals = Meal(mealName,drink,category,area,instruction,mealThumb,tags,youtube,source,imgSource,creativeCommons,dateModified)
            allMeal.add(meals)
            val ing = Ingredients(mealName,ingredientsList.toString())
            ingredients.add(ing)
            val measure = Measurements(mealName,measurementsList.toString())
            measurements.add(measure)

        }

    }

    // using onSaveInstanceState to save the state of the app
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("MealList",allMeal)
        outState.putSerializable("ingList",ingredients)
        outState.putSerializable("measureList",measurements)

    }

    // using onRestoreInstanceState to restore the state of the app
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        allMeal = savedInstanceState.getSerializable("MealList") as ArrayList<Meal>
        ingredients = savedInstanceState.getSerializable("ingList") as ArrayList<Ingredients>
        measurements = savedInstanceState.getSerializable("measureList") as ArrayList<Measurements>

        for (i in 0 until allMeal.size){

            allMealsDetails.append("Meal : ${allMeal[i].mealName} \n")
            allMealsDetails.append("DrinkAlternate : ${allMeal[i].drink} \n")
            allMealsDetails.append("Category : ${allMeal[i].category} \n")
            allMealsDetails.append("Area : ${allMeal[i].area} \n")
            allMealsDetails.append("Instructions : ${allMeal[i].instructions} \n")
            allMealsDetails.append("MealThumb : ${allMeal[i].mealThumb} \n")
            allMealsDetails.append("Tags : ${allMeal[i].tags} \n")
            allMealsDetails.append("Youtube : ${allMeal[i].youtube} \n")

            runOnUiThread {
                Picasso.get().load(allMeal[i].mealThumb).into(image)
            }

            val split = ingredients[i].ingredients.split(",")
            val split1 = measurements[i].measurements.split(",")

            for (j in 0..split.size - 1) {
                if (j == 0) {
                    allMealsDetails.append(
                        "Ingredient" + (j + 1) + ":" + split[j].removePrefix(
                            "["
                        ) + "\n"
                    )
                } else if (j == split.size - 1) {
                    allMealsDetails.append(
                        "Ingredient" + (j + 1) + ":" + split[j].removeSuffix(
                            "]"
                        ) + "\n"
                    )
                } else {
                    allMealsDetails.append("Ingredient" + (j + 1) + ":" + split[j] + "\n")
                }
            }

            for (k in 0..split1.size - 1) {
                if (k == 0) {
                    allMealsDetails.append(
                        "Measure" + (k + 1) + ":" + split1[k].removePrefix(
                            "["
                        ) + "\n"
                    )
                } else if (k == split1.size - 1) {
                    allMealsDetails.append(
                        "Measure" + (k + 1) + ":" + split1[k].removeSuffix(
                            "]"
                        ) + "\n"
                    )
                } else {
                    allMealsDetails.append("Measure" + (k + 1) + ":" + split1[k] + "\n")
                }
            }

            allMealsDetails.append("Source : ${allMeal[i].source} \n")
            allMealsDetails.append("ImageSource : ${allMeal[i].imgSource} \n")
            allMealsDetails.append("CreativeCommonsConfirmed : ${allMeal[i].CreativeCommonsConfirmed} \n")
            allMealsDetails.append("dateModified : ${allMeal[i].dateModified} \n")
            allMealsDetails.append("-----------------------------------------------------------\n\n")
        }

        if (allMealsDetails.isNotEmpty()) {
            scrollView.setBackgroundColor(resources.getColor(R.color.white))
            output.text = allMealsDetails
            allMealsDetails.clear()
        }

    }

}

/*
References

Display image from URL - https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library
Alert Dialog with EditText field - https://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
Change ScrollView colour - https://stackoverflow.com/questions/49957430/how-do-i-set-color-with-setbackgroundcolor-with-color-from-values-colors-xml
Split a String - https://www.techiedelight.com/split-string-using-delimiter-kotlin/

*/
