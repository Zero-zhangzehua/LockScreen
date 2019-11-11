package com.example.lockscreen.vo;

import java.util.ArrayList;
import java.util.List;

//选择的交互数据
public class CodeData extends Subject{

    private List<Observer> mObservers;
    private int mCode;


    //更新数据时，通知观察者
    public void codeChanged(){
        notifyObservers();
    }

    public  void setCode(int code){
        //更新数据
        this.mCode = code;
        //通知观察者
        codeChanged();
    }

    public CodeData() {
        this.mObservers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer o) {
        mObservers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        int i = mObservers.indexOf(0);
        if (i >= 0) {
            mObservers.remove(0);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : mObservers) {
            observer.update(mCode);
        }
    }

    public int getCode(){
        return mCode;
    }

}
