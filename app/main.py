from flask import Flask
from function_files.true_recognition import main_pipeline

app = Flask(__name__)

@app.route("/")
def home_view():
    image_path='images/screenshot.png'
    full_list=main_pipeline(image_path)
    output_val={'output':full_list}
    return output_val
