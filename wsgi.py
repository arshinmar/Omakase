from flask import Flask
from flask_restful import Api, Resource

app = Flask(__name__)
app = Api(app)

@app.route('/')
def dynamic_page():
    temp_dict={'1':'2'}
    return '<h1>Hello World</h1>'

if __name__ == '__main__':
    app.run()   #host='0.0.0.0', port='8000', debug=True)
