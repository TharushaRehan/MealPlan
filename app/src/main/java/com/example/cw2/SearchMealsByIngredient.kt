package com.example.cw2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class SearchMealsByIngredient : AppCompatActivity() {

    // class variables
    private lateinit var output : TextView
    private lateinit var scrollView : ScrollView
    private lateinit var ingredient: TextInputEditText
    private var allMealsDetails = java.lang.StringBuilder()
    private var allMeal : ArrayList<Meal> = arrayListOf()
    private var ingredients : ArrayList<Ingredients> = arrayListOf()
    private var measurements : ArrayList<Measurements> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealPlanAdapter: SearchMealsByIngredient.MealPlanAdapter
    private var mealPlans : ArrayList<MealPlan> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_meals_by_ingredient)

        scrollView = findViewById(R.id.scrollView)
        val retrieve = findViewById<Button>(R.id.retrieve)
        val save = findViewById<Button>(R.id.saveToDatabase)
        ingredient = findViewById(R.id.ingredient)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        retrieve.setOnClickListener {
            getMeals()
        }

        save.setOnClickListener {
            saveToDatabase()
        }

    }

    // this is the function to search meals by the ingredient
    private fun getMeals(){
        mealPlans.clear()
        allMeal.clear()
        ingredients.clear()
        measurements.clear()
        val txt = ingredient.text.toString()   // convert to a string
        // check if the text box is empty and it contains any number
        if (txt.isNotEmpty() && !txt.contains(Regex("\\d"))) {  // check the input is not empty and not contains numbers
            val stb = StringBuilder()  // create StringBuilder instance
            val urlString = "https://www.themealdb.com/api/json/v1/1/filter.php?i=$txt"    // url to filter by ingredient
            val url = URL(urlString)       // create a URL object
            val con : HttpURLConnection = url.openConnection() as HttpURLConnection  // opens a connection to that URL
            runBlocking {
                launch {
                    // run the code of the coroutine in a new thread
                    withContext(Dispatchers.IO) {
                        val bf = BufferedReader(InputStreamReader(con.inputStream)) //read the response data from the server's input stream.
                        var line: String? = bf.readLine()
                        while (line != null) {        //  read the data line by line
                            stb.append(line + "\n")
                            line = bf.readLine()
                        }
                        getMealDetails(stb) // run this method to get the meal id

                    }
                }
            }
            // if there is at least one meal, show the details if the meal
            if (mealPlans.isNotEmpty()) {
                scrollView.setBackgroundColor(resources.getColor(R.color.white))  // set background colour
                mealPlanAdapter = MealPlanAdapter(mealPlans)
                recyclerView.adapter = mealPlanAdapter

            }
        }
        // if user click Retrieve button without typing anything, show this msg
        else{
            Toast.makeText(applicationContext, "Enter Ingredient", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMealDetails(stb: java.lang.StringBuilder) {
        // this contains the full JSON returned by the Web Service
        val json = JSONObject(stb.toString())
        // check whether the returned JSON is null or not
        if (!json.isNull("meals")) {
            val jsonArray: JSONArray = json.getJSONArray("meals")
            // extract all the meals from the JSON array
            for (i in 0..jsonArray.length() - 1) {
                val meal = jsonArray.getJSONObject(i) // this is a json object
                // get the id of the meal and run the function to search meals by the id from the web service
                val id = meal.getInt("idMeal")
                getFullDetails(id)
            }
        }
    }

    // this is the function to search meals by the id
    private fun getFullDetails(id:Int){
        val stb = StringBuilder()                     // create StringBuilder instance
        val urlString = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id"  // url to search meals by id
        val url = URL(urlString)                      // create a URL object
        val con : HttpURLConnection = url.openConnection() as HttpURLConnection  // open a connection to that URL
        runBlocking {
            launch {
                // run the code of the coroutine in a new thread
                withContext(Dispatchers.IO) {
                    val bf = BufferedReader(InputStreamReader(con.inputStream))   //read the response data from the server's input stream.
                    var line: String? = bf.readLine()
                    while (line != null) {            //  read the data line by line
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
        val jsonArray:JSONArray = json.getJSONArray("meals")
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

            // get ingredients and if the value is not empty and null then add to the arraylist
            for (i in 1..20){
                val ingredient = meal.getString("strIngredient$i")
                val ingStr = ingredient.toString()
                if (ingStr != "" && ingStr != "null") {
                    ingredientsList.add(ingStr)
                }
            }

            // get measurements and if the value is not empty and null then add to the arraylist
            for (i in 1..ingredientsList.size){
                val measure = meal.getString("strMeasure$i")
                val measureStr = measure.toString()
                if (measureStr != "" && measureStr != "null") {
                    measurementsList.add(measureStr)
                }
            }

            // append meal details to a string builder
            allMealsDetails.append("Meal : $mealName \n")
            if (drink != "" && drink != "null"){
                allMealsDetails.append("DrinkAlternate : $drink \n")
            }
            allMealsDetails.append("Category : $category \n")
            allMealsDetails.append("Area : $area \n")
            allMealsDetails.append("Instructions : $instruction \n")
            allMealsDetails.append("MealThumb : $mealThumb \n")
            if (tags != "" && tags != "null") {
                allMealsDetails.append("Tags : $tags \n")
            }
            if (youtube != "" && youtube != "null") {
                allMealsDetails.append("Youtube : $youtube \n")
            }
            for (i in 0..ingredientsList.size-1){
                allMealsDetails.append("Ingredient"+(i+1)+":" + ingredientsList[i] + "\n")
            }
            for (i in 0..measurementsList.size-1){
                allMealsDetails.append("Measure"+(i+1)+":" + measurementsList[i] + "\n")
            }
            if (source != "" && source != "null") {
                allMealsDetails.append("Source : $source \n")
            }
            if (imgSource != "" && imgSource != "null") {
                allMealsDetails.append("ImageSource : $imgSource \n")
            }
            if (creativeCommons != "" && creativeCommons != "null") {
                allMealsDetails.append("CreativeCommonsConfirmed : $creativeCommons \n")
            }
            if (dateModified != "" && dateModified != "null") {
                allMealsDetails.append("dateModified : $dateModified \n")
            }
            allMealsDetails.append("-----------------------------------------------------------\n\n")

            // create objects for Meal, Ingredient, Measure and add them to a list
            val meals = Meal(mealName,drink,category,area,instruction,mealThumb,tags,youtube,source,imgSource,creativeCommons,dateModified)
            allMeal.add(meals)
            val ing = Ingredients(mealName,ingredientsList.toString())
            ingredients.add(ing)
            val measure = Measurements(mealName,measurementsList.toString())
            measurements.add(measure)

            val mealItem = MealPlan(mealThumb.toString(),allMealsDetails.toString())
            mealPlans.add(mealItem)
            allMealsDetails.clear()
        }
    }


    // when user click on the save to DB button run this function
    private fun saveToDatabase(){
        val db = Room.databaseBuilder(this,AppDatabase::class.java,"MealPlan.db").build()
        val mealDao = db.mealDao()
        val ingredientsDao = db.ingredientsDao()
        val measurementsDao = db.measurementsDao()

        runBlocking {
            launch {
                for (i in 0..allMeal.size-1){    // get data from the lists and add them to sqlite database
                    mealDao.insertMeal(allMeal[i])
                    ingredientsDao.insertIngredient(ingredients[i])
                    measurementsDao.insertMeasurements(measurements[i])
                }
            }
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
            if (allMeal[i].drink != "" && allMeal[i].drink != "null"){
                allMealsDetails.append("DrinkAlternate : ${allMeal[i].drink} \n")
            }
            allMealsDetails.append("Category : ${allMeal[i].category} \n")
            allMealsDetails.append("Area : ${allMeal[i].area} \n")
            allMealsDetails.append("Instructions : ${allMeal[i].instructions} \n")
            allMealsDetails.append("MealThumb : ${allMeal[i].mealThumb} \n")
            if (allMeal[i].tags != "" && allMeal[i].tags != "null"){
                allMealsDetails.append("Tags : ${allMeal[i].tags} \n")
            }
            if (allMeal[i].youtube != "" && allMeal[i].youtube != "null"){
                allMealsDetails.append("Youtube : ${allMeal[i].youtube} \n")
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

            if (allMeal[i].source != "" && allMeal[i].source != "null") {
                allMealsDetails.append("Source : ${allMeal[i].source} \n")
            }
            if (allMeal[i].imgSource != "" && allMeal[i].imgSource != "null") {
                allMealsDetails.append("ImageSource : ${allMeal[i].imgSource} \n")
            }
            if (allMeal[i].CreativeCommonsConfirmed != "" && allMeal[i].CreativeCommonsConfirmed != "null") {
                allMealsDetails.append("CreativeCommonsConfirmed : ${allMeal[i].CreativeCommonsConfirmed} \n")
            }
            if (allMeal[i].dateModified != "" && allMeal[i].dateModified != "null") {
                allMealsDetails.append("dateModified : ${allMeal[i].dateModified} \n")
            }
            allMealsDetails.append("-----------------------------------------------------------\n\n")

            val mealItem = MealPlan(allMeal[i].mealThumb.toString(),allMealsDetails.toString())
            mealPlans.add(mealItem)
            allMealsDetails.clear()
        }

        if (mealPlans.isNotEmpty()) {
            scrollView.setBackgroundColor(resources.getColor(R.color.white))  // set background colour
            mealPlanAdapter = MealPlanAdapter(mealPlans)
            recyclerView.adapter = mealPlanAdapter

        }

    }

    private inner class MealPlanAdapter(private val mealPlans: List<MealPlan>) :
        RecyclerView.Adapter<MealPlanAdapter.MealPlanViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.meal_item, parent, false)
            return MealPlanViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MealPlanViewHolder, position: Int) {
            val mealPlan = mealPlans[position]
            holder.bind(mealPlan)
        }

        override fun getItemCount(): Int {
            return mealPlans.size
        }

        private inner class MealPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            private val textViewMealDetails: TextView =
                itemView.findViewById(R.id.textViewMealDetails)

            fun bind(mealPlan: MealPlan) {
                // Set meal details
                textViewMealDetails.text = mealPlan.mealDetails

                // Load image from URL using Picasso
                Picasso.get().load(mealPlan.imageUrl).into(imageView)
            }
        }

    }
}
