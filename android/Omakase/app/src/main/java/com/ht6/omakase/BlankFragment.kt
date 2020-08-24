package com.ht6.omakase

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ht6.omakase.databinding.FragmentBlankBinding
import com.ht6.omakase.RecipeDetailViewModel
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_recipe_detail_old.*
import com.ht6.omakase.Recipe
import com.ht6.omakase.InjectorUtils

import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.samples.apps.sunflower.data.Plant
//import com.google.samples.apps.sunflower.databinding.FragmentPlantDetailBinding
//import com.google.samples.apps.sunflower.utilities.InjectorUtils
//import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {

//    private val args: PlantDetailFragmentArgs by navArgs()
//
//    private val plantDetailViewModel: PlantDetailViewModel by viewModels {
//        InjectorUtils.providePlantDetailViewModelFactory(requireActivity(), args.plantId)
//    }
    private val recipeDetailViewModel: RecipeDetailViewModel by viewModels {
//        InjectorUtils.provideRecipeDetailViewModelFactory(requireActivity(), "test")//args.plantId)
    val name: String = "Kittencal's Caesar Tortellini Salad"
    val instructions: String = "In a medium bowl combine the cooked tortellini with chopped lettuce, Caesar dressing and Parmesan cheese; toss well to coat. \n\nSeason with black pepper to taste and salt if desired. \n\nTop with sliced cherry tomatoes and croutons. \n\nServe immediately.\n\nDelicious!"
    val ingredients: String = "1 (9 ounce) package cheese tortellini (cooked and rinsed under cold water) \n\n1 head romaine lettuce (torn into small pieces) \n\n1 cup prepared creamy caesar salad dressing (can use less, I use Kittencal's Famous Caesar Salad) \n\n1⁄4 cup grated parmesan cheese (or to taste) \n\n10 large cherry tomatoes (cut in half) \n\n2 cups caesar-flavor croutons (see my tencal's Garlic Croutons) \n\nblack pepper (to taste) \n\nsalt (optional and to taste)"
    val imgurl: String = "https://img.sndimg.com/food/image/upload/c_thumb,q_80,w_616,h_347/v1/img/recipes/16/66/69/9oaoq9qcRTGtuhQbJ6Yq_Kittencals%20Caesar%20Tortellini%20Salad%20166669%20-%20final%203.jpg"
    InjectorUtils.provideRecipeDetailViewModelFactory(requireActivity(), name, instructions, ingredients, 30, imgurl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val binding = DataBindingUtil.inflate<FragmentBlankBinding>(
        inflater, R.layout.fragment_blank, container, false
    ).apply {
        viewModel = recipeDetailViewModel
        lifecycleOwner = viewLifecycleOwner
//        callback = object : Callback {
//            override fun add(plant: Plant?) {
//                plant?.let {
//                    hideAppBarFab(fab)
//                    RecipeDetailViewModel.addPlantToGarden()
//                    Snackbar.make(root, R.string.added_plant_to_garden, Snackbar.LENGTH_LONG)
//                        .show()
//                }
//            }
//        }

//        galleryNav.setOnClickListener { navigateToGallery() }

        var isToolbarShown = false

        // scroll change listener begins at Y = 0 when image is fully collapsed
        plantDetailScrollview.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                // User scrolled past image to height of toolbar and the title text is
                // underneath the toolbar, sof the toolbar should be shown.
                val shouldShowToolbar = scrollY > toolbar.height

                // The new state of the toolbar differs from the previous state; update
                // appbar and toolbar attributes.
                if (isToolbarShown != shouldShowToolbar) {
                    isToolbarShown = shouldShowToolbar

                    // Use shadow animator to add elevation if toolbar is shown
                    appbar.isActivated = shouldShowToolbar

                    // Show the plant name if toolbar is shown
                    toolbarLayout.isTitleEnabled = shouldShowToolbar
                }
            }
        )

        toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    createShareIntent()
                    true
                }
                else -> false
            }
        }
    }
    setHasOptionsMenu(true)

    return binding.root
}

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_blank, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonkeepgoingreal.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.mainActivity, null))
    }


    // Helper function for calling a share functionality.
    // Should be used when user presses a share button/menu item.
    @Suppress("DEPRECATION")
    private fun createShareIntent() {
//        val shareText = RecipeDetailViewModel.recipe.value.let { plant ->
//            if (plant == null) {
//                ""
//            } else {
//                getString(R.string.share_text_plant, plant.name)
//            }
//        }
        val shareText = "pizza test"
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
            .setText(shareText)
            .setType("text/plain")
            .createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(shareIntent)
    }

}