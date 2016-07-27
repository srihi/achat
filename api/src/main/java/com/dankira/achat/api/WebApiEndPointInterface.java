package com.dankira.achat.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebApiEndPointInterface
{
    @POST("/user/login")
    Call<UserProfile> loginUser(@Body UserCredentials userCredentials);

    @POST("/user/register")
    Call<UserProfile> registerUser(@Body UserProfile userProfile);

    @GET("/user/{username}")
    Call<UserProfile> getUserProfile(@Path("username") String username);

    @POST("/shoppinglist/create/{listname}")
    Call<String> addShoppingList(@Path("listname") String category);

    @GET("/shoppinglist/list")
    Call<List<ShoppingList_DTO>> getShoppingLists();

    @GET("/shoppinglist/items/list")
    Call<List<ShoppingItem_DTO>> getAllShoppingItems();

    @GET("/shoppinglist/{listid}/items/list")
    Call<List<ShoppingItem_DTO>> getShoppingItemsForList(@Path("listid") String listId);

    @POST("/shoppinglist/{listid}/items/add")
    Call<String> addItemToList(@Path("listid") String listId, @Body ShoppingItem_DTO item);

    @GET("/share/getshareid")
    Call<String> getShareCode();

    @POST("/share/verifyShare/{shareId}")
    Call<ShareStatus> verifyShare(@Path("shareId") String shareId);
}
