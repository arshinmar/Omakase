from flask import Flask

app = Flask(__name__)

@app.route("/")
def home_view():
    image_path='images/screenshot.png'
    full_list=main_pipeline(image_path)
    output_val={'output':full_list}
    return output_val

    '''
    raw_recipes_file = "RAW_recipes.pkl"
    pp_recipes_file = "PP_recipes.pkl"
    ingr_map_file = 'ingr_map.pkl'
    data_obj = recipe_data_class(raw_recipes_file, pp_recipes_file, ingr_map_file)
    '''
