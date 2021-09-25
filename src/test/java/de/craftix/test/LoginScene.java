package de.craftix.test;

import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIManager;
import de.craftix.engine.ui.components.UIAnimationComponent;
import de.craftix.engine.ui.elements.UIButton;
import de.craftix.engine.ui.elements.UIText;
import de.craftix.engine.ui.elements.UITextBox;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScene extends Scene implements ActionListener {
    private UIText title;
    private UITextBox username;
    private UITextBox password;
    private UIButton login;
    private UIButton register;

    public LoginScene() {
        setBackgroundColor(Color.GRAY);
        Font font = new Font("Arial", Font.PLAIN, 30);

        title = new UIText("Login / Register", new Font("Arial", Font.BOLD, 50), Color.WHITE, new Transform(new Vector2(0, 150)), UIAlignment.CENTER);
        username = new UITextBox("Username", new Transform(new Vector2(0, 50), new Dimension(400, 60)), UIAlignment.CENTER, UITextBox.Type.TEXT);
        password = new UITextBox("Password", new Transform(new Vector2(0, -50), new Dimension(400, 60)), UIAlignment.CENTER, UITextBox.Type.PASSWORD);
        login = new UIButton("Login", new Transform(new Vector2(-110, -150), new Dimension(185, 40)), UIAlignment.CENTER);
        register = new UIButton("Register", new Transform(new Vector2(110, -150), new Dimension(185, 40)), UIAlignment.CENTER);

        username.setFont(font);
        password.setFont(font);
        login.setFont(font);
        register.setFont(font);

        username.setMaxlength(30);
        password.setMaxlength(30);

        login.setClickListener(this);
        register.setClickListener(this);

        UIManager manager = getUIManager();
        manager.addElement(title);
        manager.addElement(username);
        manager.addElement(password);
        manager.addElement(login);
        manager.addElement(register);

        Animation animation = new Animation("default", false, false,
                new Keyframe<>(Quaternion.euler(360), 0, 1000)
        );
        title.addComponent(new UIAnimationComponent(animation));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UIAnimationComponent animationComponent = title.getComponent(UIAnimationComponent.class);
        Animation animation = animationComponent.getAnimation("default");
        animation.start();
    }
}
