'''from flask import Flask
from flask_restful import Api, Resource

app = Flask(__name__)
app = Api(app)

@app.route('/')
def dynamic_page():
    temp_dict={'1':'2'}
    return '<h1>Hello World</h1>'
'''

from flask import Flask
from true_recognition import main_pipeline
from database_files.recipe_data_class import recipe_data_class
import os

app = Flask(__name__)

@app.route("/")
def home_view():
    image_path='images/screenshot.png'
    full_list=main_pipeline(image_path)
    output_val={'output':full_list}

    raw_recipes_file = "database_files/RAW_recipes.pkl"
    pp_recipes_file = "database_files/PP_recipes.pkl"
    ingr_map_file = 'database_files/ingr_map.pkl'
    data_obj = recipe_data_class(raw_recipes_file, pp_recipes_file, ingr_map_file)

    _, ingr_list = data_obj.get_ingr_processed(full_list)
    j = data_obj.getDictOfRecipes(ingr_list, filename='recipes.json')

    return (output_val, j)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port='8000', debug=True)
    #app.run()
