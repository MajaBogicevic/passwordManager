package org.crypt.crypto.payload;

public enum PayloadVersion {
   V1("v1");
   private  final String token;
   PayloadVersion(String token){
       this.token = token;
   }
   public  String token(){
       return token;
   }
   public static PayloadVersion fromToken(String token){
       for(PayloadVersion version : values()){
           if(version.token.equals(token)){
               return  version;
           }
       }
       return null;
   }
}
