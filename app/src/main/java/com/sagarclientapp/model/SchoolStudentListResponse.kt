package com.sagarclientapp.model

import com.google.gson.annotations.SerializedName

data class SchoolStudentListResponse(

	@field:SerializedName("data")
	val data: List<SchoolStudent>? = mutableListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class School(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class SchoolStudent(

	@field:SerializedName("school_id")
	val schoolId: Int? = null,

	@field:SerializedName("grade_id")
	val gradeId: Int? = null,

	@field:SerializedName("school")
	val school: School? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("avatar")
	val avatar: String? = null,

	@field:SerializedName("grades")
	val grades: Grades? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("student_detail")
	val studentDetail: StudentDetail? = null
)

data class Grades(

	@field:SerializedName("name_ar")
	val nameAr: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Vehicle(

	@field:SerializedName("vehicle_number")
	val vehicleNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class StudentDetail(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: Int? = null,

	@field:SerializedName("vehicle")
	val vehicle: Vehicle? = null
)
