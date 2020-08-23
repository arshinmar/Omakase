'''from flask import Flask
from flask_restful import Api, Resource

app = Flask(__name__)
app = Api(app)

@app.route('/')
def dynamic_page():
    temp_dict={'1':'2'}
    return '<h1>Hello World</h1>'
'''
#Code for decryption that decrypts it back to a json
################################################################################
'''import os
import nacl.secret
import nacl.utils
import nacl.pwhash
import base64
import json

password = b"6ix Bois"
salt = b'\xdb\xb9\xf0/\xc9z\xa4\xf2\x8c:\xc9\xeb\xe1L\xbb\xbc' # our previous salt

# Generate the key:
kdf = nacl.pwhash.argon2i.kdf # our key derivation function
key = kdf(nacl.secret.SecretBox.KEY_SIZE, password, salt)

# Read the data with text mode:
with open('hack6ix-ada73c5069a0_encrypted.json', 'r') as f:
    json_dict = json.load(f)

json_decrypted = dict()
for k in json_dict.keys():
    encrypted = base64.b64decode(json_dict[k])

    # Decrypt the data:
    box = nacl.secret.SecretBox(key)
    secret_msg = box.decrypt(encrypted)
    json_decrypted[k] = secret_msg.decode("utf-8")

with open('hack6ix-ada73c5069a0_decrypted.json', 'w') as f:
    json.dump(json_decrypted, f)'''

################################################################################

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
    image_path='images/screenshot.png'
    '''credentials_raw = os.environ.get('GOOGLE_APPLICATION_CREDENTIALS')
    service_account_info = json.loads(credentials_raw)
    credentials = service_account.Credentials.from_service_account_info(service_account_info)'''
    #image_64_decode = base64.decodestring(image_64_encode)
    #image_result = open(image_path, 'wb')

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
