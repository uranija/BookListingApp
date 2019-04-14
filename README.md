# BookListingApp
## Seventh project in ABND course



The goal was to design and create the structure of a Book Listing app which would allow a user to get a list of published books on a given topic. I was given google books api in order to fetch results and display them to the user.

## Project overview
- Fetching data from an API
- Using an AsyncTask
- Parsing a JSON response
- Creating a list based on that data and displaying it to the user

## Project requirements
- Create a book listing app. A user should be able to enter a keyword, press the search button, and recieve a list of published books which relate to that keyword.
- Using of the Google Books API. This is a well-maintained API which returns information in a JSON format. An example querylooks like 
  https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1
- a simple layout, with an editable TextView and a 'search' button.
- Building the AsyncTask that queries the API. 
- Parsing the result. This will involve storing the information returned by the API in a custom class.
- Using the List and Adapter pattern to populate a list on the user's screen with the information stored in the custom objects.


![book](https://user-images.githubusercontent.com/26045797/56085198-04a3a300-5e48-11e9-9996-4a517c7993ab.png)
![book2](https://user-images.githubusercontent.com/26045797/56085199-04a3a300-5e48-11e9-880b-94abbdff6684.png)
![book3](https://user-images.githubusercontent.com/26045797/56085200-04a3a300-5e48-11e9-8a5a-870073acbe3e.png)
![book4](https://user-images.githubusercontent.com/26045797/56085201-04a3a300-5e48-11e9-824f-4ad35951e898.png)
