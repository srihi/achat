package com.dankira.achat.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebApiEndPointInterface
{
    @POST("user/login")
    Call<LoginResponse> loginUser(@Body UserCredentials userCredentials);

    @POST("user/register")
    Call<RegistrationResponse> registerUser(@Body UserProfile userProfile);

    @GET("user/{username}")
    Call<UserProfileResponse> getUserProfile(@Path("username") String username);

    @POST("shoppinglist/add")
    Call<String> addShoppingList(@Query("user_auth_token") String user_auth_token,
                                 @Body ShoppingListParam shoppingListParam);

    @GET("shoppinglist/list")
    Call<ArrayList<ShoppingList_DTO>> getShoppingLists(@Query("user_auth_token") String user_auth_token);

    @GET("shoppinglist/items/list")
    Call<ArrayList<ShoppingItem_DTO>> getShoppingItemsForList( @Query("user_auth_token") String user_auth_token,
                                                          @Query("list_guid") String list_guid);

    @GET("shoppingitems/list")
    Call<ArrayList<ShoppingItem_DTO>> getAllShoppingItems(@Query("user_auth_token") String user_auth_token);

    @POST("shoppinglist/items/add")
    Call<String> addItemToList(@Query("user_auth_token") String user_auth_token,
                               @Query("list_guid") String list_guid,
                               @Body ShoppingItem_DTO item);

    @POST("share/initShare")
    Call<ShareCodeResponse> initShare(@Query("user_auth_token") String user_auth_token,
                                      @Query("list_guid") String list_guid);

    @POST("share/completeShare}")
    Call<ShareStatus> completeShare(@Query("user_auth_token") String user_auth_token,
                                  @Query("shareId") String share_code);
}
