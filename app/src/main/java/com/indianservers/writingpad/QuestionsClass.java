package com.indianservers.writingpad;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

public class QuestionsClass {

    private int mQno,mAnswer;
    private String mQuestionDirection;
    private String mQuestion;
    private String choice1,choice2,choice3,choice4,choice5;


    public Bitmap getChoiceOne() {
        return choiceOne;
    }

    public void setChoiceOne(Bitmap choiceOne) {
        this.choiceOne = choiceOne;
    }

    public Bitmap getChoiceTwo() {
        return choiceTwo;
    }

    public void setChoiceTwo(Bitmap choiceTwo) {
        this.choiceTwo = choiceTwo;
    }

    public Bitmap getChoiceThree() {
        return choiceThree;
    }

    public void setChoiceThree(Bitmap choiceThree) {
        this.choiceThree = choiceThree;
    }

    public Bitmap getChoiceFour() {
        return choiceFour;
    }

    public void setChoiceFour(Bitmap choiceFour) {
        this.choiceFour = choiceFour;
    }

    public Bitmap getChoiceFive() {
        return choiceFive;
    }

    public void setChoiceFive(Bitmap choiceFive) {
        this.choiceFive = choiceFive;
    }

    public QuestionsClass(int mQno, int mAnswer, String mQuestionDirection, String mQuestion, Bitmap choiceOne, Bitmap choiceTwo, Bitmap choiceThree, Bitmap choiceFour, Bitmap choiceFive) {
        this.mQno = mQno;
        this.mAnswer = mAnswer;
        this.mQuestionDirection = mQuestionDirection;
        this.mQuestion = mQuestion;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = choiceThree;
        this.choiceFour = choiceFour;
        this.choiceFive = choiceFive;
    }

    private Bitmap choiceOne,choiceTwo,choiceThree,choiceFour,choiceFive;
    private int itemColor = Color.RED;

    public int getItemColor() {
        return itemColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }

    public String getChoice5() {
        return choice5;
    }

    public void setChoice5(String choice5) {
        this.choice5 = choice5;
    }

    public QuestionsClass(int mQno, int mAnswer, String mQuestionDirection, String mQuestion, String choice1, String choice2, String choice3, String choice4, String choice5) {
        this.mQno = mQno;
        this.mAnswer = mAnswer;
        this.mQuestionDirection = mQuestionDirection;
        this.mQuestion = mQuestion;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.choice5 = choice5;

    }

    public QuestionsClass(){

    }

    public int getmQno() {
        return mQno;
    }

    public void setmQno(int mQno) {
        this.mQno = mQno;
    }

    public int getmAnswer() {
        return mAnswer;
    }

    public void setmAnswer(int mAnswer) {
        this.mAnswer = mAnswer;
    }

    public String getmQuestionDirection() {
        return mQuestionDirection;
    }

    public void setmQuestionDirection(String mQuestionDirection) {
        this.mQuestionDirection = mQuestionDirection;
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }
}
