# OutboundApi

All URIs are relative to *http://glennengstrand.info/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addOutbound**](OutboundApi.md#addOutbound) | **POST** /outbound/new | create a participant news item
[**getOutbound**](OutboundApi.md#getOutbound) | **GET** /outbound/{id} | retrieve the news posted by an individual participant
[**searchOutbound**](OutboundApi.md#searchOutbound) | **POST** /outbound/search | create a participant news item


<a name="addOutbound"></a>
# **addOutbound**
> Outbound addOutbound(body)

create a participant news item

socially broadcast participant news

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Outbound**](Outbound.md)| outbound news item |

### Return type

[**Outbound**](Outbound.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getOutbound"></a>
# **getOutbound**
> List&lt;Outbound&gt; getOutbound(id)

retrieve the news posted by an individual participant

fetch a participant news

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Long**| uniquely identifies the participant |

### Return type

[**List&lt;Outbound&gt;**](Outbound.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="searchOutbound"></a>
# **searchOutbound**
> List&lt;Long&gt; searchOutbound(keywords)

create a participant news item

keyword search of participant news

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **keywords** | **String**| keywords to search for |

### Return type

**List&lt;Long&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

