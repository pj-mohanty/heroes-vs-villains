package com.example.application.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import ai.peoplecode.OpenAIConversation;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Heroes vs. Villains")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@Route(value = "")
@RouteAlias(value = "")
public class HomeView extends Composite<VerticalLayout> {
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private OpenAIConversation conversation;
    private TextField askText;
    private TextField showText;
    private Button showButton;
    private Button genQuestionButton;
    private Button buttonA;
    private Button buttonB;
    private Paragraph paragraph1;
    private Paragraph paragraph2;
    private List<String> questions;
    private static final int QUESTIONS_MAX = 10;
    private SecureRandom random;
    private ArrayList<Integer> arrayList;
    private String hero;
    private String villain;
    private static final String storyContext = " show, about the story and characters in the story itself, particularly pertaining to the hero and villain.";
    private Image image1;
    private Image image2;
    private Image showImage;
    private Button speakButton1;
    private Button speakButton2;
    private boolean hasVillain;

    private class ITSListener implements ComponentEventListener<ClickEvent<Button>> {
        @Override
        public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {

            if (buttonClickEvent.getSource().equals(speakButton1)) {
                String text = paragraph1.getText();
                if (!text.isEmpty()) {
                    UI.getCurrent().getPage().executeJs(
                            "var msg = new SpeechSynthesisUtterance($0); window.speechSynthesis.speak(msg);",
                            text
                    );
                } else {
                    Notification.show("Please enter some text to speak.");
                }
            }

            if (buttonClickEvent.getSource().equals(speakButton2)) {
                String text = paragraph2.getText();
                if (!text.isEmpty()) {
                    UI.getCurrent().getPage().executeJs(
                            "var msg = new SpeechSynthesisUtterance($0); window.speechSynthesis.speak(msg);",
                            text
                    );
                } else {
                    Notification.show("Please enter some text to speak.");
                }
            }
        }
    }

    private class AskQuestionListener implements ComponentEventListener<ClickEvent<Button>> {
        private static final String wordLimit = "100";
        @Override
        public void onComponentEvent(ClickEvent<Button> event) {

            if (event.getSource().equals(showButton)) {
                hero = conversation.askQuestion("the show " + showText.getValue(), "who is the main hero of the story? Please respond with only the character's name in less than 5 words.");
                villain = conversation.askQuestion("the show " + showText.getValue(), "who is the main villain of the story? Please respond with only the character's name in less than 5 words.");
                buttonA.setText("Ask " + hero);
                hasVillain = !conversation.askQuestion(villain,"if this context implies or states that there is a villain who has a specific name, return only \"yes\". Otherwise, return only \"no\", without the quotes").trim().equalsIgnoreCase("no");
                if (hasVillain) {
                    buttonB.setText("Ask " + villain);
                    askText.setLabel("Ask " + hero + " (the Hero) and " + villain + " (the Villain) a Question");
                } else {
                    buttonB.setText(villain);
                    askText.setLabel("Ask " + hero + " (the Hero) a Question");
                }
                questions = conversation.generateSampleQuestions(showText.getValue() + storyContext, QUESTIONS_MAX, 10);
                genQuestionButton.setText(questions.get(randomNonRepeating()));
                paragraph1.setText("");
                paragraph2.setText("");
                askText.setValue("");
                String description1 = hero + " in show " + showText.getValue();
                String imageUrl1 = generateImage(description1);
                if (imageUrl1 != null) {
                    image1.setSrc(imageUrl1);
                } else {
                    Notification.show("Image generation failed");
                }

                if (hasVillain) {
                    String description2 = villain + " in show " + showText.getValue();
                    String imageUrl2 = generateImage(description2);
                    if (imageUrl2 != null) {
                        image2.setSrc(imageUrl2);
                    } else {
                        Notification.show("Image generation failed");
                    }
                } else {
                    image2.setSrc("");
                }
            }

            String heroContext = "You are " + hero + "from the show " + showText.getValue() +  ", answering from the perspective of that character, and reply with " + wordLimit + " or less words.";
            String villainContext = "You are " + villain + " from the show " + showText.getValue() + " answering from the perspective of that character, and reply with " + wordLimit + " or less words.";

            if (event.getSource().equals(genQuestionButton)) {
                askText.setValue(genQuestionButton.getText());
                String reply1 = conversation.askQuestion(heroContext, askText.getValue());
                paragraph1.setText(reply1);

                if (hasVillain) {
                    String reply2 = conversation.askQuestion(villainContext, askText.getValue());
                    paragraph2.setText(reply2);
                }
                genQuestionButton.setText(questions.get(randomNonRepeating()));
            }

            if (event.getSource().equals(buttonA)) {
                String reply1 = conversation.askQuestion(heroContext, askText.getValue());
                paragraph1.setText(reply1);
            } else if (event.getSource().equals(buttonB) && hasVillain) {
                String reply2 = conversation.askQuestion(villainContext, askText.getValue());
                paragraph2.setText(reply2);
            }
        }
    }

    private int randomNonRepeating() {
        int num = random.nextInt(QUESTIONS_MAX);
        while (arrayList.contains(num)) {
            num = random.nextInt(QUESTIONS_MAX);
        }
        arrayList.add(num);
        if (arrayList.size() >= QUESTIONS_MAX) {
            arrayList.clear();
            questions = conversation.generateSampleQuestions(showText.getValue() + storyContext, QUESTIONS_MAX, 10);
        }
        return num;
    }

    private String generateImage(String description) {
        try {
            String apiUrl = "https://api.openai.com/v1/images/generations";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = new JSONObject()
                    .put("prompt", description)
                    .put("n", 1)
                    .put("size", "1024x1024")
                    .toString();

            connection.getOutputStream().write(payload.getBytes());

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int data = reader.read();
            while (data != -1) {
                response.append((char) data);
                data = reader.read();
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("data").getJSONObject(0).getString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HomeView() {
        random = new SecureRandom();
        conversation = new OpenAIConversation(OPENAI_API_KEY, "gpt-4o-mini");
        askText = new TextField();
        arrayList = new ArrayList<>(QUESTIONS_MAX);

        showText = new TextField("Enter the name of the show");
        showText.setWidth("99%");

        showButton = new Button("Enter");
        showButton.setWidth("min-content");
        showButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showButton.addClickListener(new AskQuestionListener());
        questions = conversation.generateSampleQuestions(showText.getValue() + storyContext, QUESTIONS_MAX, 10);

        showImage = new Image();
        showImage.setSrc("https://mir-s3-cdn-cf.behance.net/projects/404/6c75a758458267.Y3JvcCwxNjA0LDEyNTUsMjMsMTk.jpg");
        showImage.setWidth("800px");
        showImage.setHeight("400px");
        showImage.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

        image1 = new Image();
        image1.setWidth("200px");
        image1.setHeight("200px");

        image2 = new Image();
        image2.setWidth("200px");
        image2.setHeight("200px");

        Button askButton = new Button();
        buttonA = new Button();
        buttonA.setText("Ask the Hero");
        buttonA.setWidth("min-content");
        buttonA.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonA.addClickListener(new AskQuestionListener());

        buttonB = new Button();
        buttonB.setText("Ask the Villain");
        buttonB.setWidth("min-content");
        buttonB.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonB.addClickListener(new AskQuestionListener());

        genQuestionButton = new Button("Questions will appear here!");
        genQuestionButton.addClickListener(new AskQuestionListener());

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        askText.setLabel("");
        askText.setWidth("99%");
        askButton.setText("Ask");
        askButton.setWidth("min-content");
        askButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        speakButton1 = new Button("Text-to-Speech");
        speakButton2 = new Button("Text-to-Speech");

        speakButton1.addClickListener(new ITSListener());
        speakButton2.addClickListener(new ITSListener());

        paragraph1 = new Paragraph();
        paragraph2 = new Paragraph();

        String height = "420px";
        String width = "80%";

        paragraph1.setWidth(width);
        paragraph1.setHeight(height);
        paragraph1.getStyle().set("padding", "10px");
        paragraph1.getStyle().set("max-width", "none");
        paragraph1.getStyle().set("overflow-wrap", "break-word");
        paragraph1.getStyle().set("border", "1px solid black");

        paragraph2.setWidth(width);
        paragraph2.setHeight(height);
        paragraph2.getStyle().set("padding", "10px");
        paragraph2.getStyle().set("max-width", "none");
        paragraph2.getStyle().set("overflow-wrap", "break-word");
        paragraph2.getStyle().set("border", "1px solid black");

        HorizontalLayout columnsLayout = new HorizontalLayout(paragraph1, paragraph2);
        columnsLayout.setWidthFull();

        Div container1 = new Div();
        container1.getStyle().set("position", "relative");
        container1.getStyle().set("display", "flex");
        container1.getStyle().set("flex-direction", "column");
        container1.getStyle().set("align-items", "center");
        container1.add(buttonA, image1, speakButton1);

        Div container2 = new Div();
        container2.getStyle().set("position", "relative");
        container2.getStyle().set("display", "flex");
        container2.getStyle().set("flex-direction", "column");
        container2.getStyle().set("align-items", "center");
        container2.add(buttonB, image2, speakButton2);

        Div paragraphWithContainer1 = new Div(paragraph1, container1);
        Div paragraphWithContainer2 = new Div(paragraph2, container2);

        paragraphWithContainer1.getStyle().set("display", "flex");
        paragraphWithContainer1.getStyle().set("flex-direction", "row");
        paragraphWithContainer1.getStyle().set("align-items", "flex-start");
        paragraphWithContainer1.getStyle().set("gap", "20px");

        paragraphWithContainer1.getStyle().set("width", "1000px");
        paragraphWithContainer1.getStyle().set("height", "500px");
        paragraphWithContainer1.getStyle().set("overflow", "auto");

        paragraphWithContainer2.getStyle().set("display", "flex");
        paragraphWithContainer2.getStyle().set("flex-direction", "row");
        paragraphWithContainer2.getStyle().set("align-items", "flex-start");
        paragraphWithContainer2.getStyle().set("gap", "20px");

        paragraphWithContainer2.getStyle().set("width", "1000px");
        paragraphWithContainer2.getStyle().set("height", "500px");
        paragraphWithContainer2.getStyle().set("overflow", "auto");

        HorizontalLayout paragraphsLayout = new HorizontalLayout(paragraphWithContainer1, paragraphWithContainer2);
        paragraphsLayout.setWidthFull();
        paragraphsLayout.setSpacing(true);

        getContent().add(showImage);
        getContent().add(showText);
        getContent().add(showButton);
        getContent().add(genQuestionButton);
        getContent().add(askText);
        getContent().add(paragraphsLayout);
        getContent().add(columnsLayout);
        getStyle().set("background-color", "#f0f8ff");
    }
}
