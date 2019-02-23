package partlyapp.techpeg.com.partly.ViewModels;

import android.arch.lifecycle.ViewModel;

import partlyapp.techpeg.com.partly.Models.Member;
import partlyapp.techpeg.com.partly.Models.Pool;

public class PoolViewModel extends ViewModel {

    private Pool currPool;

    public Pool getCurrentPool(){
        return currPool;
    }

    public Pool createNewPool(String name){
        return new Pool(name);
    }

    public void addMemberToPool(String name){
        currPool.getMembers().add(new Member(name));
    }

}
