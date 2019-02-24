package partlyapp.techpeg.com.partly.Singleton;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.neovisionaries.ws.client.WebSocketState;

import partlyapp.techpeg.com.partly.Models.Member;
import partlyapp.techpeg.com.partly.Models.Pool;

public class PoolSingleton {
    private static  PoolSingleton ourInstance;

    public static PoolSingleton getInstance() {
        if(ourInstance==null)
            ourInstance=new PoolSingleton();
        return ourInstance;
    }

    private PoolSingleton() {
    }

    private Pool currPool;

    public Pool getCurrentPool(){
        if(currPool==null)
            currPool=new Pool();
        return currPool;
    }

    public Pool createNewPool(String name){
        return new Pool(name);
    }

    public void addMemberToPool(String name){
        Member member = new Member(name);
        member.save();
        currPool.getMembers().add(member);
    }

    public String extractPoolName(String token){
        JWT poolJWT=new JWT(token);
        Claim poolClaim=poolJWT.getClaim("name");
        String poolName= poolClaim.asString();
        this.currPool.setName(poolName);
        return poolName;
    }



}
