package com.sanjivani.nurseryplantmanagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sanjivani.nurseryplantmanagement.databinding.ActivityAddPlantBinding

class AddPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlantBinding // ViewBinding reference
    private var plantImageUri: String? = null  // To hold the image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the category from intent
        val category = intent.getStringExtra("CATEGORY")

        // Set a title for the activity based on the category
        title = "Add $category Plant"

        // Handle image selection (launching the gallery)
        binding.plantImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        // Save plant details
        binding.saveButtonPlant.setOnClickListener {
            val name = binding.plantname.text.toString()
            val description = binding.plantDescriptionEditText.text.toString()
            val price = binding.plantPriceEditText.text.toString()

            // Validate inputs
            if (name.isEmpty() || description.isEmpty() || price.isEmpty() || plantImageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a Plant object
            val plant = Plant(name, description, price, plantImageUri!!)

            // Add plant to the corresponding category
            when (category) {
                "Decoration" -> PlantRepository.decorationPlants.add(plant)
                "Flower" -> PlantRepository.flowerPlants.add(plant)
                "Fruit" -> PlantRepository.fruitPlants.add(plant)
                "Seeds" -> PlantRepository.seeds.add(plant)
            }

            // Show success message
            Toast.makeText(this, "$category Plant added successfully", Toast.LENGTH_SHORT).show()

            // Return to the previous screen
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("UPDATED_PLANT_LIST", true)
            })
            finish()
        }
    }

    // Image picker result launcher
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the image URI from the result data
                val imageUri = result.data?.data
                plantImageUri = imageUri.toString()

                // Set the image to the ImageView
                binding.plantImageView.setImageURI(imageUri)
            }
        }
}
