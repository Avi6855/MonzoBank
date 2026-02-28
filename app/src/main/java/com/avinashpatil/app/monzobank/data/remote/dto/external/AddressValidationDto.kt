package com.avinashpatil.app.monzobank.data.remote.dto.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressValidationRequestDto(
    @Json(name = "address_line_1")
    val addressLine1: String,
    
    @Json(name = "address_line_2")
    val addressLine2: String?,
    
    @Json(name = "city")
    val city: String,
    
    @Json(name = "state")
    val state: String?,
    
    @Json(name = "postal_code")
    val postalCode: String,
    
    @Json(name = "country")
    val country: String,
    
    @Json(name = "validation_level")
    val validationLevel: String? // "basic", "enhanced", "full"
)

@JsonClass(generateAdapter = true)
data class AddressValidationResponseDto(
    @Json(name = "validation_id")
    val validationId: String,
    
    @Json(name = "is_valid")
    val isValid: Boolean,
    
    @Json(name = "confidence_score")
    val confidenceScore: Double, // 0.0 to 1.0
    
    @Json(name = "standardized_address")
    val standardizedAddress: StandardizedAddressDto?,
    
    @Json(name = "validation_results")
    val validationResults: AddressValidationResultsDto,
    
    @Json(name = "suggestions")
    val suggestions: List<AddressSuggestionDto>?,
    
    @Json(name = "geocoding")
    val geocoding: GeocodingDto?,
    
    @Json(name = "delivery_info")
    val deliveryInfo: DeliveryInfoDto?,
    
    @Json(name = "validation_timestamp")
    val validationTimestamp: String
)

@JsonClass(generateAdapter = true)
data class StandardizedAddressDto(
    @Json(name = "address_line_1")
    val addressLine1: String,
    
    @Json(name = "address_line_2")
    val addressLine2: String?,
    
    @Json(name = "city")
    val city: String,
    
    @Json(name = "state")
    val state: String,
    
    @Json(name = "state_code")
    val stateCode: String?,
    
    @Json(name = "postal_code")
    val postalCode: String,
    
    @Json(name = "postal_code_extension")
    val postalCodeExtension: String?,
    
    @Json(name = "country")
    val country: String,
    
    @Json(name = "country_code")
    val countryCode: String,
    
    @Json(name = "formatted_address")
    val formattedAddress: String
)

@JsonClass(generateAdapter = true)
data class AddressValidationResultsDto(
    @Json(name = "address_line_1_valid")
    val addressLine1Valid: Boolean,
    
    @Json(name = "city_valid")
    val cityValid: Boolean,
    
    @Json(name = "state_valid")
    val stateValid: Boolean,
    
    @Json(name = "postal_code_valid")
    val postalCodeValid: Boolean,
    
    @Json(name = "country_valid")
    val countryValid: Boolean,
    
    @Json(name = "deliverable")
    val deliverable: Boolean,
    
    @Json(name = "residential")
    val residential: Boolean?,
    
    @Json(name = "commercial")
    val commercial: Boolean?,
    
    @Json(name = "vacant")
    val vacant: Boolean?,
    
    @Json(name = "errors")
    val errors: List<ValidationErrorDto>?,
    
    @Json(name = "warnings")
    val warnings: List<String>?
)

@JsonClass(generateAdapter = true)
data class ValidationErrorDto(
    @Json(name = "field")
    val field: String,
    
    @Json(name = "error_code")
    val errorCode: String,
    
    @Json(name = "error_message")
    val errorMessage: String,
    
    @Json(name = "severity")
    val severity: String // "error", "warning", "info"
)

@JsonClass(generateAdapter = true)
data class AddressSuggestionDto(
    @Json(name = "suggestion_id")
    val suggestionId: String,
    
    @Json(name = "address")
    val address: StandardizedAddressDto,
    
    @Json(name = "confidence_score")
    val confidenceScore: Double,
    
    @Json(name = "match_type")
    val matchType: String, // "exact", "close", "partial"
    
    @Json(name = "changes_made")
    val changesMade: List<String>
)

@JsonClass(generateAdapter = true)
data class GeocodingDto(
    @Json(name = "latitude")
    val latitude: Double,
    
    @Json(name = "longitude")
    val longitude: Double,
    
    @Json(name = "accuracy")
    val accuracy: String, // "rooftop", "street", "city", "region"
    
    @Json(name = "timezone")
    val timezone: String?
)

@JsonClass(generateAdapter = true)
data class DeliveryInfoDto(
    @Json(name = "delivery_point")
    val deliveryPoint: String?,
    
    @Json(name = "carrier_route")
    val carrierRoute: String?,
    
    @Json(name = "delivery_point_barcode")
    val deliveryPointBarcode: String?,
    
    @Json(name = "usps_deliverable")
    val uspsDeliverable: Boolean?,
    
    @Json(name = "delivery_days")
    val deliveryDays: List<String>? // ["monday", "tuesday", etc.]
)