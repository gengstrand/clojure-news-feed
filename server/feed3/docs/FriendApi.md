# FriendApi

All URIs are relative to *http://glennengstrand.info/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addFriend**](FriendApi.md#addFriend) | **POST** /friends/new | create a new friendship
[**getFriend**](FriendApi.md#getFriend) | **GET** /friends/{id} | retrieve the list of friends for an individual participant


<a name="addFriend"></a>
# **addFriend**
> Friend addFriend(body)

create a new friendship

friends are those participants who receive news

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Friend**](Friend.md)| friendship to be created |

### Return type

[**Friend**](Friend.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getFriend"></a>
# **getFriend**
> List&lt;Friend&gt; getFriend(id)

retrieve the list of friends for an individual participant

fetch participant friends

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Long**| uniquely identifies the participant |

### Return type

[**List&lt;Friend&gt;**](Friend.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

