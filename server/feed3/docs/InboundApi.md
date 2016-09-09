# InboundApi

All URIs are relative to *http://glennengstrand.info/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getInbound**](InboundApi.md#getInbound) | **GET** /inbound/{id} | retrieve the inbound feed for an individual participant


<a name="getInbound"></a>
# **getInbound**
> Inbound getInbound(id)

retrieve the inbound feed for an individual participant

fetch inbound feed by id

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Long**| uniquely identifies the participant |

### Return type

[**Inbound**](Inbound.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

