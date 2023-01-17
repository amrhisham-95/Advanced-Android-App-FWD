package com.example.my3rdappdesign

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //determine the width that custom Button will be take it
    private var widthOfView = 0

    private var txtWidth = 0f

    //determine the height of the custom rectangle button View
    private var heightOfView = 0

    //determine size of the title Text that drawing on the custom Button
    private var sizeOfTheText: Float = resources.getDimension(R.dimen.default_text_size)


    //determine the title of the text that is display on the custom button
    private var titleOfButton: String = "Download"

    //the width for progress operation will be animated from it 0F
    private var theWidthOfTheProgress = 0f

    //the start point for progress circle will be animated from it 0F
    private var theProgressCircle = 0f

    //animatorTool to make animation
    private var animatorTool = ValueAnimator()

    //getting background color for custom button before clicking the button
    private var backGroundButtonColor =
        ResourcesCompat.getColor(resources, R.color.backgroundCustomButton, null)

    // changing background color for custom button during the loading operation
    private var backGroundButtonColorLoading =
        ResourcesCompat.getColor(resources, R.color.yellow, null)

    //changing background color for the circle
    private var backGroundButtonColorCircle =
        ResourcesCompat.getColor(resources, R.color.white, null)


    //making variable from (sealed class) ButtonState to change the title of the customButton(in content_main.xml) before & during & after loading is finished
    //And making the animation On the button for different states (when clicked,at loading, when finished)
    var stateOfTheButton: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            //when the custom button is clicked chane the title from default value(Download) to (Button Is Clicked) but it does more fast dose not show
            ButtonState.Clicked -> {
                titleOfButton = "Button Is Clicked"
                //invalidate(means redraw on screen if something changes, it needs to be reflected on screen)
                invalidate()
            }
            ButtonState.Loading -> {
                //during the loading operation, the title of the custom Button is changed to (We are loading)
                titleOfButton = resources.getString(R.string.button_loading)
                //that returns a ValueAnimator that animates between float Values (0F and widthSize)
                animatorTool = ValueAnimator.ofFloat(0f, widthOfView.toFloat())
                //duration is the time that the animation take it
                animatorTool.duration = 4500
                //adds a listener to the valueAnimator that are sent update events through the life of an animation
                animatorTool.addUpdateListener {
                    //changes the theWidthOfTheProgress of the loading action during animation process
                    theWidthOfTheProgress = it.animatedValue as Float
                    //changes on the theProgressCircle , 360 is a complete angle as it forms a circle around a point
                    theProgressCircle = (widthOfView / 360) * theWidthOfTheProgress
                    //invalidate(means redraw on screen if something changes, it needs to be reflected on screen)
                    invalidate()
                }
                //onAnimatedEnd override function when the animation is finished put the progressWidth returns again to 0F and again restart animation until the loading finishes
                animatorTool.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                        //the width of progress process returns again to 0F to start another cycle of animation
                        theWidthOfTheProgress = 0f
                        //by using sealed class ButtonState that has 3 state, as long as the loading does not finished yet, the animation repeats itself again and again until loading finishes
                        //the previous process will repeating (ButtonState.Loading ->)
                        if (stateOfTheButton == ButtonState.Loading) {
                            //put the stateOfTheButton= loading state that means the animation of progress will repeats again
                            stateOfTheButton = ButtonState.Loading
                        }
                    }
                })
                //to start the animation on button
                animatorTool.start()
            }
            //if the animation process is completed, cancel the animation and put progressWidth 0F and the circle to 0F also and put the title to (Downloaded)
            ButtonState.Completed -> {
                //to cancel the animation when the download is completed
                animatorTool.cancel()
                //the width for progress operation returns to 0f when loading is completed
                theWidthOfTheProgress = 0f
                //the start point for progress circle returns to 0f when loading is completed
                theProgressCircle = 0f
                //put title to (Downloaded) when loading is completed
                titleOfButton = "Downloaded"
                //invalidate(means redraw on screen if something changes, it needs to be reflected on screen)
                invalidate()
            }
        }
    }


    //initial values that implemented first when the project is opened
    init {
        //liking between the attrs.xml files and the colors of the custom button
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backGroundButtonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            backGroundButtonColorLoading = getColor(R.styleable.LoadingButton_loadingButtonColor, 0)
            backGroundButtonColorCircle =
                getColor(R.styleable.LoadingButton_loadingButtonCircleColor, 0)
        }
    }

    // to create a new paint with default settings (drawing tool)
    private val paintingTool = Paint().apply {
        //smooths out the edges of what is being drawn, but it has no impact on the interior of the shape
        isAntiAlias = true
        //getting the dimension of the textSize from resources File (100sp) - textSize is a built in function
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    //override fun (onDraw) is used to draw the canvas the Ordering matters designTheRectangle first then design the progress loading then design the titleText then design the progress circle
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //function that draw the rectangle shape and color of background for the custom Button(Loading Button)
        designTheRectangle(canvas)
        //function that draw the rectangle progress shape for the custom Button(Loading Button) and color of background
        designTheProgressRectangleLoading(canvas)
        //function that draw the title Text for the custom Button(Loading Button) and color of title
        designTheButtonTitle(canvas)
        //function that draw the progress circle On the custom Button(Loading Button) and color of background
        designTheProgressCircle(canvas)
    }

    //to tell android how you want your custom button view to be dependent the layout constraints provided by the parent,and to learn what those layout constraints are.
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        //means the layout_Width and layout_height values of the custom Button were set to specific value (w,h)
        widthOfView = w
        heightOfView = h
        setMeasuredDimension(w, h)
    }

    //designTheRectangle function is used to draw the default rectangle by drawRect() and put the color of it's background
    private fun designTheRectangle(canvas: Canvas?) {
        //determine the backgroundColor of Rectangle custom Button
        paintingTool.color = backGroundButtonColor
        //there are 5 parameters for drawRec():1st(left side of rectangle)-2nd(top side of rectangle)-3rd(right side of rectangle)-4th(bottom side of rectangle)-5th(painterTool to draw rectangle)
        //put the right side of rectangle equals to widthOfView and put the bottom side equals to heightOfView that means the custom Button will take the height and width of the view
        canvas?.drawRect(0f, 0f, widthOfView.toFloat(), heightOfView.toFloat(), paintingTool)
    }

    //designTheButtonTitle function is used to draw the text of the title of customButton and it's background color
    private fun designTheButtonTitle(canvas: Canvas?) {
        //determine the Color of title
        paintingTool.color = Color.BLACK

        txtWidth = paintingTool.measureText(titleOfButton)

        canvas?.drawText(
            titleOfButton, widthOfView/2 - txtWidth/2,
            heightOfView/2 - (paintingTool.descent() + paintingTool.ascent())/2 ,paintingTool)


    }

    //designTheProgressRectangleLoading function is used to draw the Rectangle when the progress operation is started and it's background color
    private fun designTheProgressRectangleLoading(canvas: Canvas?) {
        //determine the backgroundColor of progressRectangleLoading
        paintingTool.color = backGroundButtonColorLoading

        //there are 5 parameters for drawRec():1st(left side of rectangle)-2nd(top side of rectangle)-3rd(right side of rectangle)-4th(bottom side of rectangle)-5th(painterTool to draw rectangle)
        //put the left side equals to theWidthOfTheProgress that means the progress animation direction starts from left of rectangle to the right
        //put the top side equals to heightOfView that means the height pf progress Rectangle takes the height of original rectangle
        canvas?.drawRect(theWidthOfTheProgress, heightOfView.toFloat(), 0f, 0f, paintingTool)
    }

    //designTheProgressCircle function is used to draw the progress circle that occurs during the loading operation and it's background color
    private fun designTheProgressCircle(canvas: Canvas?) {
        canvas?.save()
        //coordination x and y where the progress circle is occurs on the custom Button
        //(2*widthOfView)/3 puts the circle in the 1/3 from the rectangle view at the right on the x axis
        //heightOfView/4 puts the circle at the center of the rectangle view on y axis
        canvas?.translate((2 * widthOfView.toFloat()) / 3, heightOfView.toFloat() / 4)
        //determine the backgroundColor of progress circle
        paintingTool.color = backGroundButtonColorCircle
        //drawing the progressCircle by using drawArc
        //useCenter (true) that is means include the center of the oval in the arc and close it if it is being stroked
        //sweepAngle is (theProgressCircle*0.360f) 360 degree is an entire cycle for the circle
        //we can change start angle to any value we need to start point angle for example 30 degree the circle will start sweep from 30 degree
        //RectF() to draw the Arc
        canvas?.drawArc(RectF(0f, 0f, 75f, 75f), 0f, theProgressCircle * 0.360f, true, paintingTool)
        canvas?.restore()
    }
}