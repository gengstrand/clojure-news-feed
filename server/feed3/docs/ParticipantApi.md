# ParticipantApi

All URIs are relative to *http://glennengstrand.info/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addParticipant**](ParticipantApi.md#addParticipant) | **POST** /participant/new | create a new participant
[**getParticipant**](ParticipantApi.md#getParticipant) | **GET** /participant/{id} | retrieve an individual participant


<a name="addParticipant"></a>
# **addParticipant**
> Participant addParticipant(body)

create a new participant

a participant is someone who can post news to friends

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Participant**](Participant.md)| participant to be created |

### Return type

[**Participant**](Participant.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getParticipant"></a>
# **getParticipant**
> Participant getParticipant(id)

retrieve an individual participant

fetch a participant by id

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Long**| uniquely identifies the participant |

### Return type

[**Participant**](Participant.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

