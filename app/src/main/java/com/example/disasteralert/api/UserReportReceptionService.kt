import com.example.disasteralert.api.UserReportReceptionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserReportReceptionService {
    @GET("report/user_history")
    fun getUserReportHistory(
        @Query("user_id") userId: String,
        @Query("limit") limit: Int = 50
    ): Call<UserReportReceptionResponse>
}
