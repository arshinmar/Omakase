# Omakase

<img src="https://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/001/194/952/datas/gallery.jpg" width="375" height="250"/> <img src="https://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/001/195/377/datas/gallery.jpg" width="150" height="250"/> <img src="https://i.imgur.com/uuLR3Wj.png" width="150" height="250"/>
<img src="https://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/001/195/341/datas/gallery.jpg" width="130" height="250"/>

This repository was created as part of our submission for the HackThe6ix 2020 hackathon. It received MLH's Best Use of Google Cloud prize for its exemplary use of the Google Cloud Vision API. 

## Inspiration

On numerous occasions, we have each found ourselves staring blankly into the fridge with no idea of what to make. Given some combination of ingredients, what type of good food can I make, and how?
What It Does

Omakase recommends recipes based on the food that is in your fridge right now.
What We Learned

Most of the members in our group were inexperienced in mobile app development and backend. Through this hackathon, we learned a lot of new skills in Kotlin, HTTP requests, setting up a server, and more.

## How We Built It

We started with an Android application with access to the user’s phone camera. This app was created using Kotlin and XML. Android’s ViewModel Architecture and the X library were used. This application uses an HTTP PUT request to send the image to a Heroku server through a Flask web application. This server then leverages machine learning and food recognition from the Google Cloud Vision API to split the image up into multiple regions of interest. These images were then fed into the API again, to classify the objects in them into specific ingredients, while circumventing the API’s imposed query limits for ingredient recognition. We split up the image by shelves using an algorithm to detect more objects. A list of acceptable ingredients was obtained. Each ingredient was mapped to a numerical ID and a set of recipes for that ingredient was obtained. We then algorithmically intersected each set of recipes to get a final set of recipes that used the majority of the ingredients. These were then passed back to the phone through HTTP.
Challenges You Faced

Our largest challenge came from creating a server and integrating the API endpoints for our Android app. We also had a challenge with the Google Vision API since it is only able to detect 10 objects at a time. To move past this limitation, we found a way to segment the fridge into its individual shelves. Each of these shelves were analysed one at a time, often increasing the number of potential ingredients by a factor of 4-5x. Configuring the Heroku server was also difficult.
Built With

<img src="https://i.imgur.com/bMGFlAM.png" width="333" height="200"/>

## What We Are Proud Of

We were able to gain skills in Kotlin, HTTP requests, servers, and using APIs. The moment that made us most proud was when we put an image of a fridge that had only salsa, hot sauce, and fruit, and the app provided us with three tasty looking recipes including a Caribbean black bean and fruit salad that uses oranges and salsa.
Challenges You Faced

Our largest challenge came from creating a server and integrating the API endpoints for our Android app. We also had a challenge with the Google Vision API since it is only able to detect 10 objects at a time. To move past this limitation, we found a way to segment the fridge into its individual shelves. Each of these shelves were analysed one at a time, often increasing the number of potential ingredients by a factor of 4-5x. Configuring the Heroku server was also difficult.

<img src="https://challengepost-s3-challengepost.netdna-ssl.com/photos/production/software_photos/001/195/364/datas/gallery.jpg" width="450" height="200"/>

## Whats Next

We have big plans for our app in the future. Some next steps we would like to implement is allowing users to include their dietary restriction and food preferences so we can better match the recommendation to the user. We also want to make this app available on smart fridges, currently fridges, like Samsung’s, have a function where the user inputs the expiry date of food in their fridge. This would allow us to make recommendations based on the soonest expiring foods.
