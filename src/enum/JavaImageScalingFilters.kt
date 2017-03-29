package enum

import com.mortennobel.imagescaling.ResampleFilter
import com.mortennobel.imagescaling.ResampleFilters

/**
 * Created by vladstarikov on 3/29/17.
 */

enum class JavaImageScalingFilters(val filter: ResampleFilter) {
    Bell(ResampleFilters.getBellFilter()),
    BiCubic(ResampleFilters.getBiCubicFilter()),
    BiCubicHighFreqResponse(ResampleFilters.getBiCubicHighFreqResponse()),
    Box(ResampleFilters.getBoxFilter()),
    BSpline(ResampleFilters.getBSplineFilter()),
    Hermite(ResampleFilters.getHermiteFilter()),
    Mitchell(ResampleFilters.getMitchellFilter()),
    Lanczos3(ResampleFilters.getLanczos3Filter()),
    Triangle(ResampleFilters.getTriangleFilter()),
}
