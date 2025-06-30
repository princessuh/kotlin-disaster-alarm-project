import com.example.disasteralert.api.UserReportReceptionResponse
import retrofit2.Call
import retrofit2.http.GET

interface UserReportReceptionService {
    @GET("userReport/history")
    fun getUserReportHistory(): Call<UserReportReceptionResponse>
}
