// video link ->

package com.example.cw2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addMealsToDB = findViewById<Button>(R.id.addToDB)
        val searchByIngredient = findViewById<Button>(R.id.searchByIngredient)
        val searchForMeals = findViewById<Button>(R.id.searchForMeals)

        // create the database
        val db = Room.databaseBuilder(this,AppDatabase::class.java,"MealPlan.db").build()
        val mealDao = db.mealDao()
        val ingredientsDao = db.ingredientsDao()
        val measurementsDao = db.measurementsDao()

        // when user clicks on Add Meals to DB button the following data will save to sqlite database
        addMealsToDB.setOnClickListener {
            // run the code of the coroutine in a new thread
            runBlocking {
                launch {
                    // insert meal details one by one to the database
                    val meal1 = Meal("Sweet and Sour Pork","null","Pork","Chines",
                        "Preparation\n1. Crack the egg into a bowl. Separate the egg white and yolk.\n2. Slice the pork tenderloin into ips.\n3. Prepare the marinade using a pinch of salt, one teaspoon of starch, two teaspoons of light soy sauce, and an egg white.\n4. Marinade the pork ips for about 20 minutes.\n5. Put the remaining starch in a bowl. Add some water and vinegar to make a starchy sauce.\nSweet and Sour Pork Cooking Inuctions\n1. Pour the cooking oil into a wok and heat to 190\u00b0C (375\u00b0F). Add the marinated pork ips and fry them until they turn brown. Remove the cooked pork from the wok and place on a plate.\n2. Leave some oil in the wok. Put the tomato sauce and white sugar into the wok, and heat until the oil and sauce are fully combined.\n3. Add some water to the wok and thoroughly heat the sweet and sour sauce before adding the pork ips to it.\n4. Pour in the starchy sauce. Stir-fry all the ingredients until the pork and sauce are thoroughly mixed together.\n5. Serve on a plate and add some coriander for decoration.",
                        "https://www.themealdb.com/images/media/meals/1529442316.jpg","Sweet","https://www.youtube.com/watch?v=mdaBIhgEAMo","null","null","null","null")
                    val meal1Ingredients = arrayListOf("Pork","Egg","Water","Salt","Sugar","Soy Sauce","Starch","Tomato Puree","Vinegar","Coriander")
                    val meal1measurements = arrayListOf("200g","1","Dash","1/2 tsp","1 tsp","10g","10g","30g","10g","Dash")
                    val meal1ingredients = Ingredients("Sweet and Sour Pork",meal1Ingredients.toString())
                    val meal1Measurements = Measurements("Sweet and Sour Pork",meal1measurements.toString())
                    mealDao.insertMeal(meal1)
                    ingredientsDao.insertIngredient(meal1ingredients)
                    measurementsDao.insertMeasurements(meal1Measurements)

                    val meal2 = Meal("Chicken Marengo","null","Chicken","French","Heat the oil in a large flameproof casserole dish and stir-fry the mushrooms until they start to soften. Add the chicken legs and cook briefly on each side to colour them a little.\nPour in the passata, crumble in the stock cube and stir in the olives. Season with black pepper \\u2013 you shouldn\\u2019t need salt. Cover and simmer for 40 mins until the chicken is tender. Sprinkle with parsley and serve with pasta and a salad, or mash and green veg, if you like.",
                    "https://www.themealdb.com/images/media/meals/qpxvuq1511798906.jpg","null","https://www.youtube.com/watch?v=U33HYUr-0Fw","https://www.bbcgoodfood.com/recipes/3146682/chicken-marengo","null","null","null")
                    val m2ingredients = arrayListOf("Olive oil","Mushrooms","Chicken Legs","Passata","Chicken stock cube","Black olives","Parsley")
                    val m2measurements = arrayListOf("1 tbs","300g","4","500g","1","100g","chopped")
                    val m2Ingredients = Ingredients("Chicken Marengo",m2ingredients.toString())
                    val m2Measurements = Measurements("Chicken Marengo",m2measurements.toString())
                    mealDao.insertMeal(meal2)
                    ingredientsDao.insertIngredient(m2Ingredients)
                    measurementsDao.insertMeasurements(m2Measurements)

                    val meal3 = Meal("Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber","null","Beef","Vietnamese","Add'l ingredients: mayonnaise, siracha\r\n\r\n1\r\n\r\nPlace rice in a fine-mesh sieve and rinse until water runs clear. Add to a small pot with 1 cup water (2 cups for 4 servings) and a pinch of salt. Bring to a boil, then cover and reduce heat to low. Cook until rice is tender, 15 minutes. Keep covered off heat for at least 10 minutes or until ready to serve.\r\n\r\n2\r\n\r\nMeanwhile, wash and dry all produce. Peel and finely chop garlic. Zest and quarter lime (for 4 servings, zest 1 lime and quarter both). Trim and halve cucumber lengthwise; thinly slice crosswise into half-moons. Halve, peel, and medium dice onion. Trim, peel, and grate carrot.\r\n\r\n3\r\n\r\nIn a medium bowl, combine cucumber, juice from half the lime, \u00bc tsp sugar (\u00bd tsp for 4 servings), and a pinch of salt. In a small bowl, combine mayonnaise, a pinch of garlic, a squeeze of lime juice, and as much sriracha as you\u2019d like. Season with salt and pepper.\r\n\r\n4\r\n\r\nHeat a drizzle of oil in a large pan over medium-high heat. Add onion and cook, stirring, until softened, 4-5 minutes. Add beef, remaining garlic, and 2 tsp sugar (4 tsp for 4 servings). Cook, breaking up meat into pieces, until beef is browned and cooked through, 4-5 minutes. Stir in soy sauce. Turn off heat; taste and season with salt and pepper.\r\n\r\n5\r\n\r\nFluff rice with a fork; stir in lime zest and 1 TBSP butter. Divide rice between bowls. Arrange beef, grated carrot, and pickled cucumber on top. Top with a squeeze of lime juice. Drizzle with sriracha mayo.",
                        "https://www.themealdb.com/images/media/meals/z0ageb1583189517.jpg","null","null","null","null","null","null")
                    val m3ingredients = arrayListOf("Rice","Onion","Lime","Garlic Clove","Cucumber","Carrots","Ground Beef","Soy sauce")
                    val m3measurements = arrayListOf("White","1","1","3","1","3 oz","1 lb","2 oz")
                    val m3Ingredients = Ingredients("Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",m3ingredients.toString())
                    val m3Measurements = Measurements("Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",m3measurements.toString())
                    mealDao.insertMeal(meal3)
                    ingredientsDao.insertIngredient(m3Ingredients)
                    measurementsDao.insertMeasurements(m3Measurements)

                    val meal4 = Meal("Leblebi Soup","null","Vegetarian","Tunisian","Heat the oil in a large pot. Add the onion and cook until translucent.\nDrain the soaked chickpeas and add them to the pot together with the vegetable stock. Bring to the boil, then reduce the heat and cover. Simmer for 30 minutes.\nIn the meantime toast the cumin in a small ungreased frying pan, then grind them in a mortar. Add the garlic and salt and pound to a fine paste.\nAdd the paste and the harissa to the soup and simmer until the chickpeas are tender, about 30 minutes.\nSeason to taste with salt, pepper and lemon juice and serve hot.",
                        "https://www.themealdb.com/images/media/meals/x2fw9e1560460636.jpg","Soup","https://www.youtube.com/watch?v=BgRifcCwinY","http://allrecipes.co.uk/recipe/43419/leblebi--tunisian-chickpea-soup-.aspx","null","null","null")
                    val m4ingredients = arrayListOf("Olive Oil","Onion","Chickpeas","Vegetable Stock","Cumin","Garlic","Salt","Harissa Spice","Pepper","Lime")
                    val m4measurements = arrayListOf("2 tbs","1 medium finely diced","250g","1.5L","1 tsp","5 cloves","1/2 tsp","1 tsp","Pinch","1/2")
                    val m4Ingredients = Ingredients("Leblebi Soup",m4ingredients.toString())
                    val m4Measurements = Measurements("Leblebi Soup",m4measurements.toString())
                    mealDao.insertMeal(meal4)
                    ingredientsDao.insertIngredient(m4Ingredients)
                    measurementsDao.insertMeasurements(m4Measurements)
                }
            }
        }

        // when user clicks on Search by Ingredient button, start that activity
        searchByIngredient.setOnClickListener {
            val activity = Intent(this,SearchMealsByIngredient::class.java)
            startActivity(activity)
        }

        // when user clicks on Search for Meals button, start that activity
        searchForMeals.setOnClickListener {
            val activity = Intent(this,SearchForMeals::class.java)
            startActivity(activity)
        }
    }
}
