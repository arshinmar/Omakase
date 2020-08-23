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
from function_files.true_recognition import main_pipeline

app = Flask(__name__)

@app.route("/")
def home_view():
    image_path='images/screenshot.png'
    full_list=main_pipeline(image_path)
    output_val={'output':full_list}
    return output_val

if __name__ == '__main__':
    app.run()   #host='0.0.0.0', port='8000', debug=True)
