import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BrickBreaker extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_RADIUS = 10;
    private static final int ROWS = 5;
    private static final int COLUMNS = 10;
    private static final int BRICK_WIDTH = 60;
    private static final int BRICK_HEIGHT = 20;

    private double ballX;
    private double ballY;
    private double ballDX;
    private double ballDY;
    private double paddleX;
    private double paddleY;
    private boolean[][] bricks;
    private boolean running;
    private boolean moveLeft;
    private boolean moveRight;
    private int score;
    private int highestScore = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Text gameTitle = new Text("Brick Breaker");
        gameTitle.setFont(Font.font("Arial", 36));
        gameTitle.setFill(Color.DARKBLUE);

        Button newGameButton = createRoundedButton("New Game");
        Button aboutButton = createRoundedButton("About");
        Button developersButton = createRoundedButton("Developers");

        newGameButton.setOnAction(e -> startGameStage(new Stage())); // Launch the game
        aboutButton.setOnAction(e -> showAboutStage(new Stage()));  // Show About Stage
        developersButton.setOnAction(e -> showDevelopersStage(new Stage()));  // Show Developers Stage

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(gameTitle, newGameButton, aboutButton, developersButton);

        Scene introScene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Brick Breaker");
        primaryStage.setScene(introScene);
        primaryStage.show();
    }

    private Button createRoundedButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 16));
        button.setStyle("-fx-background-color: lightblue; -fx-text-fill: black; "
                + "-fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 10 20;");
        return button;
    }

    private void startGameStage(Stage gameStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Add score text
        Text scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font("Arial", 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setX(10);
        scoreText.setY(30);
        root.getChildren().add(scoreText);

        // Add highest score text
        Text highestScoreText = new Text("Highest Score: " + highestScore);
        highestScoreText.setFont(Font.font("Arial", 24));
        highestScoreText.setFill(Color.WHITE);
        highestScoreText.setX(WIDTH - 210);  
        highestScoreText.setY(30);
        root.getChildren().add(highestScoreText);

        Scene gameScene = new Scene(root);
        gameStage.setScene(gameScene);
        gameStage.setTitle("Brick Breaker Game");
        gameStage.show();

        setup();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            run(gc);
            scoreText.setText("Score: " + score); // Update score display
            highestScoreText.setText("Highest Score: " + highestScore); // Update highest score display
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            } else if (e.getCode() == KeyCode.R) {
                setup();
            }
        });

        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = false;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = false;
            }
        });
    }

    private void setup() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballDX = 3;
        ballDY = 3;
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        paddleY = HEIGHT - PADDLE_HEIGHT - 10;
        bricks = new boolean[ROWS][COLUMNS];
        running = true;
        moveLeft = false;
        moveRight = false;
        score = 0;
        initBricks();
    }

    private void initBricks() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                bricks[i][j] = true;
            }
        }
    }

    private void run(GraphicsContext gc) {
        if (!running) return;

        gc.setFill(Color.ROSYBROWN);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.GREY);
        gc.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        if (running) {
            gc.setFill(Color.SILVER);
            gc.fillOval(ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (bricks[i][j]) {
                    gc.setFill(Color.BEIGE);   
                    gc.fillRect(j * BRICK_WIDTH + 30, i * BRICK_HEIGHT + 50, BRICK_WIDTH - 1, BRICK_HEIGHT - 1);
                }
            }
        }
        //for ball movement

        ballX += ballDX;
        ballY += ballDY;

        if (ballX < 0 || ballX > WIDTH - BALL_RADIUS * 2) {
            ballDX = -ballDX;
        }

        if (ballY < 0) {
            ballDY = -ballDY;
        }

        if (ballY > HEIGHT - BALL_RADIUS * 2) {
            running = false;
            if (score > highestScore) {
                highestScore = score;
                gc.setFill(Color.WHITE);
                gc.setFont(new Font(36));
                gc.fillText("Congrats! New High Score: " + highestScore, WIDTH / 2 - 200, HEIGHT / 2);
                gc.setFont(new Font(20));
                gc.fillText("Press 'R' to Restart", WIDTH / 2 - 200, HEIGHT / 2 + 50); //+ 50: spacing b/w lines
            } else {
                gc.setFill(Color.WHITE);
                gc.setFont(new Font(36));
                gc.fillText("Game Over - Press 'R' to Restart", WIDTH / 2 - 200, HEIGHT / 2);
            }
        }

        if (ballY + BALL_RADIUS * 2 >= paddleY && ballY + BALL_RADIUS * 2 <= paddleY + PADDLE_HEIGHT &&
            ballX + BALL_RADIUS >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballDY = -ballDY;
        } //collision with the padle

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (bricks[i][j]) {
                    if (ballX + BALL_RADIUS * 2 >= j * BRICK_WIDTH + 30 &&
                        ballX <= j * BRICK_WIDTH + 30 + BRICK_WIDTH - 1 &&
                        ballY + BALL_RADIUS * 2 >= i * BRICK_HEIGHT + 50 &&
                        ballY <= i * BRICK_HEIGHT + 50 + BRICK_HEIGHT - 1) {
                        bricks[i][j] = false;
                        ballDY = -ballDY;
                        score += 10; // Increase score
                    }
                }
            }
        }

        if (moveLeft && paddleX > 0) {
            paddleX -= 5;
        }

        if (moveRight && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += 5;
        }
    }

    private void showAboutStage(Stage aboutStage) {
        Text aboutHeading = new Text("About the Game: Brick Breaker");
        aboutHeading.setFont(Font.font("Arial", 24));
        aboutHeading.setFill(Color.DARKBLUE);
        aboutHeading.setTextAlignment(TextAlignment.CENTER);

        Text aboutContent = new Text(
                """
        		Get ready to smash, crash, and bash your way to glory with Brick Breaker — the game where breaking stuff is not only allowed, but "highly encouraged"! Armed with nothing but a trusty paddle and a bouncy ball, your mission is simple: obliterate all the bricks standing in your way. It’s like demolition day, but way more fun (and no clean-up required).
        		
        		Why You'll Love It:
        		- Break Stuff (Legally!): Channel your inner wrecking ball as you aim, hit, and destroy every last brick.
        		- Scores That Keep You Guessing: Just when you think you’ve nailed it, the game throws in trickier layouts and sneaky surprises.
        		- Power-Ups Galore: From laser-shooting paddles to balls that multiply like rabbits, these boosts will have you shouting, “Where have you been all my life?!”
        		- Sights That Pop: Vibrant colors, and cool animations that make you feel like a gaming legend.
        		- Challenge Accepted: Beat your own high score, or better yet, and be the champion of Brick Breaker Game.
        		
        		How To Play:
        		- Use the left and right arrow keys to move the paddle.
        		- Break all the bricks to achieve maximum highest score.
        		- Enjoy power-ups and special bricks for a dynamic game experience.
        		
        		Whether you're killing time, avoiding work, or just love watching things shatter into tiny pixels, Brick Breaker is the game you didn't know you needed. Grab your paddle, aim the ball, and let the destruction begin!

                """
        );
        aboutContent.setFont(Font.font("Arial", 14));
        aboutContent.setFill(Color.BLACK);
        aboutContent.setWrappingWidth(600);
        aboutContent.setTextAlignment(TextAlignment.JUSTIFY);


        VBox aboutLayout = new VBox(20);
        aboutLayout.setAlignment(Pos.CENTER);
        aboutLayout.getChildren().addAll(aboutHeading, aboutContent);

        Scene aboutScene = new Scene(aboutLayout, 800, 600);
        aboutStage.setTitle("About Brick Breaker");
        aboutStage.setScene(aboutScene);
        aboutStage.show();
    }

    private void showDevelopersStage(Stage developersStage) {
        Text devHeading = new Text("Developers");
        devHeading.setFont(Font.font("Arial", 24));
        devHeading.setFill(Color.DARKBLUE);
        devHeading.setTextAlignment(TextAlignment.CENTER);

        Text devContent = new Text(
                """
        		Meet the Developers

        		Behind every great game lies a team of creative minds, and Brick Breaker is no exception. Developed by a group of 2nd-year Software Engineering students, this project showcases our passion for innovation, problem-solving, and building something truly fun.  

        		The Team of Dreamers and Doers:  
        		- Iqra Hafeez	(23-SE-45)
        		The artistic soul who breathed life into the visuals, making the game both dynamic and delightful. Also, the QA expert who left no bug unturned, perfecting every aspect of gameplay.  
        		- Aqsa Najeeb	(23-SE-51)
        		The mastermind behind game logic, ensuring smooth mechanics and flawless functionality. The coding enthusiast who optimized performance and added the finishing touches.

        		A Journey of Learning and Growth:  
        		This project was more than just an assignment — it was an adventure into the realm of game development. It gave us the chance to apply key concepts like object-oriented programming, event-driven design, and teamwork in a practical, engaging way.  

        		We’re thrilled to share the result of our hard work with you and hope that Brick Breaker brings as much excitement to your screen as it brought to our development process.
        		"""
        );
        devContent.setFont(Font.font("Arial", 14));
        devContent.setFill(Color.BLACK);
        devContent.setWrappingWidth(600);
        devContent.setTextAlignment(TextAlignment.JUSTIFY);

        VBox devLayout = new VBox(20);
        devLayout.setAlignment(Pos.CENTER);
        devLayout.getChildren().addAll(devHeading, devContent);

        Scene devScene = new Scene(devLayout, 800, 600);
        developersStage.setTitle("Developers");
        developersStage.setScene(devScene);
        developersStage.show();
    }
}