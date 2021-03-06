package org.osmdroid.bonuspack.overlays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * A ground overlay is an image that is fixed to a map.
 * Mimics the GroundOverlay class from Google Maps Android API v2 as much as possible. Main differences:<br/>
 * - Doesn't support: Z-Index, setPositionFromBounds<br/>
 * - image can be any standard Android Drawable, instead of the BitmapDescriptor introduced in Maps API. <br/>
 *
 * @author M.Kergall
 */
public class GroundOverlay extends Overlay {

    public final static float NO_DIMENSION = -1.0f;
    protected Drawable mImage;
    protected GeoPoint mPosition;
    protected float mBearing;
    protected float mWidth, mHeight;
    protected float mTransparency;
    protected Point mPositionPixels, mSouthEastPixels;

    public GroundOverlay(Context ctx) {
        this(new DefaultResourceProxyImpl(ctx));
    }

    public GroundOverlay(final ResourceProxy resourceProxy) {
        super(resourceProxy);
        mWidth = 10.0f;
        mHeight = NO_DIMENSION;
        mBearing = 0.0f;
        mTransparency = 0.0f;
        mPositionPixels = new Point();
        mSouthEastPixels = new Point();
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable image) {
        mImage = image;
    }

    public GeoPoint getPosition() {
        return mPosition.clone();
    }

    public void setPosition(GeoPoint position) {
        mPosition = position.clone();
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        mBearing = bearing;
    }

    public void setDimensions(float width) {
        mWidth = width;
        mHeight = NO_DIMENSION;
    }

    public void setDimensions(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    public float getHeight() {
        return mHeight;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getTransparency() {
        return mTransparency;
    }

    public void setTransparency(float transparency) {
        mTransparency = transparency;
    }

    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        if (mImage == null)
            return;

        if (mHeight == NO_DIMENSION) {
            mHeight = mWidth * mImage.getIntrinsicHeight() / mImage.getIntrinsicWidth();
        }

        final Projection pj = mapView.getProjection();

        pj.toPixels(mPosition, mPositionPixels);
        GeoPoint pEast = mPosition.destinationPoint(mWidth, 90.0f);
        GeoPoint pSouthEast = pEast.destinationPoint(mHeight, -180.0f);
        pj.toPixels(pSouthEast, mSouthEastPixels);
        int width = mSouthEastPixels.x - mPositionPixels.x;
        int height = mSouthEastPixels.y - mPositionPixels.y;
        mImage.setBounds(-width / 2, -height / 2, width / 2, height / 2);

        mImage.setAlpha(255 - (int) (mTransparency * 255));

        drawAt(canvas, mImage, mPositionPixels.x, mPositionPixels.y, false, -mBearing);
    }

}
