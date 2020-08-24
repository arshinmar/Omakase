from flask import Flask
from true_recognition import main_pipeline
from database_files.recipe_data_class import recipe_data_class
import os
from google.oauth2 import service_account
import json

app = Flask(__name__)

@app.route("/")
def home_view():
    '''credential_path = "hack6ix-ada73c5069a0_decrypted.json"
    os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = credential_path'''

    '''credentials_raw = os.environ.get('GOOGLE_APPLICATION_CREDENTIALS')
    service_account_info = json.loads(credentials_raw)
    credentials = service_account.Credentials.from_service_account_info(service_account_info)'''
    #image_64_decode = base64.decodestring(image_64_encode)
    #image_result = open(image_path, 'wb')
    image_path='images/20200823_093157-min.jpg'
    full_list=main_pipeline(image_path)
    output_val={'output':full_list}
    '''
    raw_recipes_file = "database_files/RAW_recipes.pkl"
    pp_recipes_file = "database_files/PP_recipes.pkl"
    ingr_map_file = 'database_files/ingr_map.pkl'
    data_obj = recipe_data_class(raw_recipes_file, pp_recipes_file, ingr_map_file)

    _, ingr_list = data_obj.get_ingr_processed(full_list)
    j = data_obj.getDictOfRecipes(ingr_list, filename='recipes.json')

    return (output_val, j)'''
    return output_val

if __name__ == '__main__':
    app.run(host='0.0.0.0', port='8000', debug=True)
    #app.run()
