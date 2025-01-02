# Project Title: Project 3 -- GenAI with OpenAI and Vaadin
## Heroes vs Villains

# Overview of the Project:
This project uses OpenAI's API and Vaadin in Java to create an interactive, questions-and-answers-focused conversation experience with the hero and villain of a user's choosing, via the context of a story that the user inputs. It is fun to hear opposite perspectives on similar issues/questions within the story the characters are apart of. Further, OpenAI will generate an image of the character from the story, and users have the option to have the text the character responds with read to them via the "Text-to-Speech" button.
	
# How to Deploy the App:

This app requires that Java is installed on the user's machine, and that they have an internet browser available to them. Also, to make the app fully functional, the user must replace the word "demo" in the line

    private static final String OPENAI_API_KEY = "demo";

with their OpenAI API key. Having access to a Java IDE is helpful here for managing the integration with the external PeopleCodeOpenAI library used in this project.

# Development Process:
The development of this app started with the User Interface (UI), and transitioned more to a Backend focus over time. The thought process behind this was the idea that we needed to know what we were programming before we could actually build it. We knew we wanted to have a hero and a villain, in the form of a set of two of characters responding to a common question.	I liked the idea of clicking buttons to enter text, and having a wide range of space for the text input available to the user. We enjoyed the blend of the technical and the aesthetic in this project, building an enjoyable UI for the user, and making a clear, logical collection of code for use in this and future projects. 

In terms of a clear, logical Backend to the application, in the onComponentEvent() function, we spent a significant period of time refining the quality of the prompt to give us a relevant and interesting answer to the question written by the user, or entered by the user via clicking on the AI generated question about the story having to do with the characters at hand. We also made sure that the Villain would produce no response when there is no identifiable villain in the story. One major challenge was that it took significant time to figure out the correct logic and prompts to use with OpenAI to determine from a generated source whether there was actually a villain or not based on the name (or lack there of) of the villain, and checking to see specifically whether that answer was "yes" or "no". Also, We have used two AI-generated images for both characters, below the "ask character" button, above the "text to speech" button, and to the right of the paragraph. We felt that, given the dynamic range of input for context of a story and for the main hero and villain, that having a group of images would not be enough to capture the range of possibility. As such, We decided to make the images for each character AI generated for ease of use in matching the context more appropriately to the subject matter. 

Finally, We wanted to maximize the linking of object together in context of the Paragraph object, so that we could have multiple paragraph-button-image-and-text-to-speech buttons linked, such that they would look exactly the same on both sides of a center divide. We do this using HorizontalLayout and Div objects as part of Vaadin to create a grid like structure, linking each object relative to the other both vertically and horizontally.

# Description of What We Completed:
One of our assumptions about this program and this story-based idea is that there will always be a hero in the story, so we did not have to account for a lack of having a hero in our code.

In terms of extra credit for the project, we completed the use of System Text-to-Speech to read the characters AI-generated words and the use of AI-generated images to describe the characters visually.

# Test Plan:

1. Enter "Lord of The Rings" in the Show Title box
2. Click "Enter" in the button below
3. Observe the Images created for the character, make sure the content of the images is correct 
4. Click on the AI generated question
5. Read the AI generated text responses
6. Click "Text-to-Speech" on the first response
7. Enter "Friends" in the Show Title box
8. Click "Enter" in the button below
9. Verify that there is no Villain character in the show.
10. Type in a question to the question box and ask Rachel Green
11. Click on the Villain's "Ask..." box and verify that no text is generated
12. Done!

# Test Video (link):

USFCA Google Drive Link
1. Program Output -- https://drive.google.com/file/d/1VmeVcp9PHqYm6HdatSwBmsS37GDQny44/view?usp=sharing
2. Program Source Code -- https://drive.google.com/file/d/1y_pxKsQuD4wlPqbPoBvNjlOMGQGjjTOm/view?usp=sharing