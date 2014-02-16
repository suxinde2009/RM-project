
#include <jni.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>


#define pi 3.14159265358979324
#define x_pi (pi * 3000.0 / 180.0)

//
// Krasovsky 1940
//
// a = 6378245.0, 1/f = 298.3
// b = a * (1 - f)
// ee = (a^2 - b^2) / a^2;
#define a 6378245.0
#define ee 0.00669342162296594323

bool outOfChina(double lat, double lon) {
	if (lon < 72.004 || lon > 137.8347)
		return true;
	if (lat < 0.8293 || lat > 55.8271)
		return true;
	return false;
}

double transformLat(double x, double y) {
	double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x));
	ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
	ret += (20.0 * sin(y * pi) + 40.0 * sin(y / 3.0 * pi)) * 2.0 / 3.0;
	ret += (160.0 * sin(y / 12.0 * pi) + 320 * sin(y * pi / 30.0)) * 2.0 / 3.0;
	return ret;
}

double transformLon(double x, double y) {
	double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(abs(x));
	ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
	ret += (20.0 * sin(x * pi) + 40.0 * sin(x / 3.0 * pi)) * 2.0 / 3.0;
	ret += (150.0 * sin(x / 12.0 * pi) + 300.0 * sin(x / 30.0 * pi)) * 2.0 / 3.0;
	return ret;
}

//
// World Geodetic System ==> Mars Geodetic System
JNIEXPORT jdoubleArray JNICALL Java_com_jan_rm_utils_CoordsTransform_getGCJ02fromWGS84(JNIEnv *env, jobject obj, jdouble wgLat, jdouble wgLon){

	jdoubleArray result;
	result = (*env)->NewDoubleArray(env, 2);
	if(result == NULL) return NULL;

	jdouble temp[2];

	if (outOfChina(wgLat, wgLon)){
		temp[0] = wgLat;
		temp[1] = wgLon;
	}else{
		double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
		double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
		double radLat = wgLat / 180.0 * pi;
		double magic = sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * cos(radLat) * pi);
		temp[0] = wgLat + dLat;
		temp[1] = wgLon + dLon;
	}

	(*env)->SetDoubleArrayRegion(env, result, 0, 2, temp);

	return result;
}

JNIEXPORT jdoubleArray JNICALL Java_com_jan_rm_utils_CoordsTransform_getBD09fromGCJ02(JNIEnv *env, jobject obj, jdouble lat, jdouble lng){

	jdoubleArray result;
	result = (*env)->NewDoubleArray(env, 2);
	if(result == NULL) return NULL;

	jdouble temp[2];

	double x = lng;
	double y = lat;
	double z = sqrt(x * x + y * y) + 0.00002 * sin(y * x_pi);
	double theta = atan2(y, x) + 0.000003 * cos(x * x_pi);

	temp[0] = z * sin(theta) + 0.006;
	temp[1] = z * cos(theta) + 0.0065;

	(*env)->SetDoubleArrayRegion(env, result, 0, 2, temp);

	return result;
}

JNIEXPORT jdoubleArray JNICALL Java_com_jan_rm_utils_CoordsTransform_getGCJ02fromBD09(JNIEnv *env, jobject obj, jdouble lat, jdouble lng){

	jdoubleArray result;
	result = (*env)->NewDoubleArray(env, 2);
	if(result == NULL) return NULL;

	jdouble temp[2];

	double x = lng - 0.0065;
	double y = lat - 0.006;
	double z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi);
	double theta = atan2(y, x) - 0.000003 * cos(x * x_pi);

	temp[0] = z * sin(theta);
	temp[1] = z * cos(theta);

	(*env)->SetDoubleArrayRegion(env, result, 0, 2, temp);

	return result;
}
