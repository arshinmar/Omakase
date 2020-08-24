package com.ht6.omakase

//package com.example.android.camera2.basic.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ht6.android.camera.utils.GenericListAdapter
import com.ht6.android.camera.utils.decodeExifOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.logging.Logger
import javax.net.ssl.HttpsURLConnection
import javax.security.auth.callback.Callback
import kotlin.math.max


class ImageViewerFragment : Fragment() {

    /** AndroidX navigation arguments */
    private val args: ImageViewerFragmentArgs by navArgs()

    /** Default Bitmap decoding options */
    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        // Keep Bitmaps at less than 1 MP
        if (max(outHeight, outWidth) > DOWNSAMPLE_SIZE) {
            val scaleFactorX = outWidth / DOWNSAMPLE_SIZE + 1
            val scaleFactorY = outHeight / DOWNSAMPLE_SIZE + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    /** Bitmap transformation derived from passed arguments */
    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(args.orientation) }

    /** Flag indicating that there is depth data available for this image */
    private val isDepth: Boolean by lazy { args.depth }

    /** Data backing our Bitmap viewpager */
    private val bitmapList: MutableList<Bitmap> = mutableListOf()

    private fun imageViewFactory() = ImageView(requireContext()).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }
    private fun imageButtonFactory() = ImageButton(requireContext()).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

//    private fun imageButtonFactory() = ImageButton(requireContext()).apply {
//        layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//    }
   // private val floatingActionButton = FloatingActionButton(requireContext())

    //private var a: View? = null, var b:android.view.View? = null

//    override fun onCreateView(
////        inflater: LayoutInflater,
////        container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View? = ViewPager2(requireContext()).apply {
////        // Populate the ViewPager and implement a cache of two media items
////        offscreenPageLimit = 2
////        adapter = GenericListAdapter(
////            bitmapList,
////            itemViewFactory = { imageViewFactory() }) { view, item, _ ->
////            view as ImageView
////            Glide.with(view).load(item).into(view)
////        }
//////        val floatingActionButton = FloatingActionButton(requireContext())
//////
//////        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//////        layoutParams.setMargins(32, 32, 32, 32)
//////        floatingActionButton.layoutParams = layoutParams
//////        floatingActionButton.setImageResource(android.R.drawable.ic_dialog_email)
//////        floatingActionButton.setOnClickListener {
//////            // We are showing only toast message. However, you can do anything you need.
//////            Toast.makeText(applicationContext, "You clicked Floating Action Button", Toast.LENGTH_SHORT).show()
//////        }
////       // View? = inflater.inflate(R.layout.fragment_camera, container, false)
////        //val viewa = inflater.inflate(R.layout.fragment_buttons, container)
////
////    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val viewa = inflater.inflate(R.layout.fragment_buttons, container, false)
        //val floatingActionButton = FloatingActionButton(requireContext())

        //inflater.inflate(R.layout.fragment_buttons, container, false)
       // imageButtonFactory
//        val toolbar = Toolbar(requireContext())
        
        return ViewPager2(requireContext()).apply {
            // Populate the ViewPager and implement a cache of two media items
            offscreenPageLimit = 2
            adapter = GenericListAdapter(
                bitmapList,
                itemViewFactory = { imageViewFactory() }) { view, item, _ ->
                view as ImageView
                Glide.with(view).load(item).into(view)
            }
        //val floatingActionButton = FloatingActionButton(requireContext())
            //fragment_nav?.addView(floatingActionButton)
//
//        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        layoutParams.setMargins(32, 32, 32, 32)
//        floatingActionButton.layoutParams = layoutParams
//        floatingActionButton.setImageResource(android.R.drawable.ic_dialog_email)
//        floatingActionButton.setOnClickListener {
//            // We are showing only toast message. However, you can do anything you need.
//            Toast.makeText(applicationContext, "You clicked Floating Action Button", Toast.LENGTH_SHORT).show()
//        }
            // View? = inflater.inflate(R.layout.fragment_camera, container, false)
            //val viewa = inflater.inflate(R.layout.fragment_buttons, container)

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//                floatingActionButton.setOnClickListener {
//            // We are showing only toast message. However, you can do anything you need.
//            Toast.makeText(getActivity()?.getApplicationContext(), "You clicked Floating Action Button", Toast.LENGTH_SHORT).show()
//        }

//        view as ViewPager2
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            // Load input image file
//            val inputBuffer = loadInputBuffer()
//
//            // Load the main JPEG image
//            addItemToViewPager(view, decodeBitmap(inputBuffer, 0, inputBuffer.size))
//
//            // If we have depth data attached, attempt to load it
//            if (isDepth) {
//                try {
//                    val depthStart = findNextJpegEndMarker(inputBuffer, 2)
//                    addItemToViewPager(
//                        view, decodeBitmap(
//                            inputBuffer, depthStart, inputBuffer.size - depthStart
//                        )
//                    )
//
//                    val confidenceStart = findNextJpegEndMarker(inputBuffer, depthStart)
//                    addItemToViewPager(
//                        view, decodeBitmap(
//                            inputBuffer, confidenceStart, inputBuffer.size - confidenceStart
//                        )
//                    )
//
//                } catch (exc: RuntimeException) {
//                    Log.e(TAG, "Invalid start marker for depth or confidence data")
//                }
//            }
//        }
        // convert ot bas64
//        inputBuffer
        val inputBuffer = loadInputBuffer()
        val bitmapThing = decodeBitmap(inputBuffer, 0, inputBuffer.size)
        //val base64img = getBase64Image(bitmapThing)
        getBase64Image(bitmapThing, complete = { base64Image ->GlobalScope.launch(Dispatchers.Default) {
            val LOG = Logger.getLogger("new base64 image")
            LOG.warning(base64Image)
            LOG.warning(args.orientation.toString())
//            uploadImageToImgur(bitmapThing)
        }})
        uploadImageToImgur(bitmapThing)
//        val LOG = Logger.getLogger("Image stuff")
//        LOG.warning(String(inputBuffer))
        // navigate to next destination
        //findNavController().navigate(ImageViewerFragmentDirections.actionImageViewerFragmentToImageConfirmFragment())
        //findNavController().navigate(ImageviewerFragmentdirections.actionImageViewerFragmentTo)
        // new navigation stuff
//        SystemClock.sleep(2000)
//        findNavController().navigate(ImageViewerFragmentDirections.actionImageViewerFragmentToScrollingFragment())



    }

    /** Utility function used to read input file into a byte array */
    private fun loadInputBuffer(): ByteArray {
        val inputFile = File(args.filePath)
        return BufferedInputStream(inputFile.inputStream()).let { stream ->
            ByteArray(stream.available()).also {
                stream.read(it)
                stream.close()
            }
        }
    }

    /** Utility function used to add an item to the viewpager and notify it, in the main thread */
    private fun addItemToViewPager(view: ViewPager2, item: Bitmap) = view.post {
        bitmapList.add(item)
        view.adapter!!.notifyDataSetChanged()
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true
        )
    }

    // new functions for uploading to server
    private fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
        GlobalScope.launch {
            val outputStream = ByteArrayOutputStream()
//            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))
        }
    }

    private fun uploadImageToImgur(image: Bitmap) {
        getBase64Image(image, complete = { base64Image ->
            GlobalScope.launch(Dispatchers.Default) {
//                val url = URL("https://api.imgur.com/3/image")
//                val url = URL("https://test-app-omakase.herokuapp.com/")
                //URL("https://google.com").readText()
//                https://test-app-omakase.herokuapp.com/
                /////////sdfkjsdhf
                //WORKS
//                val serverURL: String = "https://test-app-omakase.herokuapp.com/"
//                val url = URL(serverURL)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.connectTimeout = 300000
//                connection.connectTimeout = 300000
//                connection.doOutput = true
////                base64Image
//                val postData: ByteArray = base64Image.toByteArray(StandardCharsets.UTF_8)
//
//                connection.setRequestProperty("charset", "utf-8")
//                connection.setRequestProperty("Content-length", postData.size.toString())
//                connection.setRequestProperty("Content-Type", "application/json")
//
//                try {
//                    val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
//                    outputStream.write(postData)
//                    outputStream.flush()
//                } catch (exception: Exception) {
//
//                }
///////a/s/df/ds/f

                val serverURL: String = "https://postman-echo.com/post"
                val url = URL(serverURL)

                val boundary = "Boundary-${System.currentTimeMillis()}"
                val httpsURLConnection =
                    withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                //httpsURLConnection.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
                httpsURLConnection.setRequestProperty(
                    "Content-Type",
                    "multipart/form-data; boundary=$boundary"
                )

                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true

//                var body = ""
//                body += "--$boundary\r\n"
////                body += "Content-Disposition:form-data; name=\"image\""
////                body += "\r\n\r\n$base64Image\r\n"
//                body += "Content-Disposition:form-data; name=\"foo1\""
//                body += "\r\n\r\n\"Testin body 1\"\r\n\r\n"
//                body += "Content-Disposition:form-data; name=\"foo2\""
//                body += "\r\n\r\nTesting body 2\r\n"
//                body += "--$boundary--\r\n"

//                "foo1": "Testin body 1\r\nContent-Disposition:form-data; name=\"foo2\"\r\n\r\nTesting body 2"
//                var body = ""
//                body += "--$boundary\r\n"
////                body += "Content-Disposition:form-data; name=\"image\""
////                body += "\r\n\r\n$base64Image\r\n"
//                body += "Content-Disposition:form-data; name=\"foo1\""
//                body += "\r\n\r\nTestin body 1\r\n"
//                body += "Content-Disposition:form-data; name=\"foo2\""
//                body += "\r\n\r\nTesting body 2\r\n"
//                body += "--$boundary--\r\n"

                //works with one foo1 form
                var body = ""
                body += "--$boundary\r\n"
//                body += "Content-Disposition:form-data; name=\"image\""
//                body += "\r\n\r\n$base64Image\r\n"
                body += "Content-Disposition:form-data; name=\"foo1\""
                body += "\r\n\r\nTestin body 1\r\n"
//                body += "Content-Disposition:form-data; name=\"foo2\""
//                body += "\r\n\r\nTesting body 2\r\n\r\n"
                body += "--$boundary--\r\n"

                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                withContext(Dispatchers.IO) {
                    outputStreamWriter.write(body)
                    outputStreamWriter.flush()
                }

                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                //val data = jsonObject.getJSONObject("data")
                val args = jsonObject.getJSONObject("args")
                Log.d("TAG", "Link is : ${args.toString()}")
                //Log.d("TAG", "Link is : ${data.getString("link")}")

//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.connectTimeout = 300000
//                connection.connectTimeout = 300000
//                connection.doOutput = true
////                base64Image
                //val postData: ByteArray = base64Image.toByteArray(StandardCharsets.UTF_8)
//                val postData = "Is this working pleasejalkdsjflksafjlkasjflksajflk".toByteArray(StandardCharsets.UTF_8)
//
//
//                connection.setRequestProperty("charset", "utf-8")
//                connection.setRequestProperty("Content-length", postData.size.toString())
//                connection.setRequestProperty("Content-Type", "application/json")
//
//                try {
//                    val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
//                    outputStream.write(postData)
//                    outputStream.flush()
//                } catch (exception: Exception) {
//
//                }
//
//                var client = OkHttpClient()
//                var request = OkHttpRequest(client)
//                val url = "http://api.plos.org/search?q=title:%22Drosophila%22%20and%20body:%22RNA%22&fl=id,abstract&wt=json&indent=on"
//
//                request?.GET(url, object: Callback {
//                    override fun onResponse(call: Call?, response: Response) {
//                        val responseData = response.body()?.string()
//                        runOnUiThread{
//                            try {
//                                var json = JSONObject(responseData)
//                                println("Request Successful!!")
//                                println(json)
//                                val responseObject = json.getJSONObject("response")
//                                val docs = json.getJSONArray("docs")
//                                //this@MainActivity.fetchComplete()
//                                this@CameraActvity.fetchComplete()
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call?, e: IOException?) {
//                        println("Request Failure.")
//                    }
//                })


//                PUT /echo/put/json HTTP/1.1
//                Authorization: Bearer mt0dgHmLJMVQhvjpNXDyA83vA_PxH23Y
//                Accept: application/json
//                Content-Type: application/json
//                Content-Length: 85
//                Host: reqbin.com
//
//                {
//                    "Id": 12345,
//                    "Customer": "John Smith",
//                    "Quantity": 1,
//                    "Price": 10.00
//                }

//                val url = URL("https://www.google.com/")
//
//                val boundary = "Boundary-${System.currentTimeMillis()}"
//
//                val httpsURLConnection =
//                    withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
//                //httpsURLConnection.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
//                httpsURLConnection.setRequestProperty(
//                    "Content-Type",
//                    "multipart/form-data; boundary=$boundary"
//                )

//                httpsURLConnection.requestMethod = "POST"
//                httpsURLConnection.requestMethod = "GET"
//                httpsURLConnection.doInput = true
//                httpsURLConnection.doOutput = true

//                var body = ""
//                body += "--$boundary\r\n"
//                body += "Content-Disposition:form-data; name=\"image\""
//                body += "\r\n\r\n$base64Image\r\n"
//                body += "--$boundary--\r\n"
                //stuff new
//                var body = ""
//                body += "--$boundary\r\n"
//                body += "\"image\""
////                body += "\r\n\r\n$base64Image\r\n"
//                body += "\r\n\r\ntest\r\n"
//                body += "--$boundary--\r\n"


//                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
//                withContext(Dispatchers.IO) {
//                    outputStreamWriter.write(body)
//                    outputStreamWriter.flush()
//                }

//                val response = httpsURLConnection.inputStream.bufferedReader()
//                    .use { it.readText() }  // defaults to UTF-8
//                val jsonObject = JSONTokener(response).nextValue() as JSONObject
//                val data = jsonObject.getJSONObject("data")

//                Log.d("TAG", "Link is : ${data.getString("link")}")

            }
        })
    }



    companion object {
        private val TAG = ImageViewerFragment::class.java.simpleName

        /** Maximum size of [Bitmap] decoded */
        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP

        /** These are the magic numbers used to separate the different JPG data chunks */
        private val JPEG_DELIMITER_BYTES = arrayOf(-1, -39)

        /**
         * Utility function used to find the markers indicating separation between JPEG data chunks
         */
        private fun findNextJpegEndMarker(jpegBuffer: ByteArray, start: Int): Int {

            // Sanitize input arguments
            assert(start >= 0) { "Invalid start marker: $start" }
            assert(jpegBuffer.size > start) {
                "Buffer size (${jpegBuffer.size}) smaller than start marker ($start)" }

            // Perform a linear search until the delimiter is found
            for (i in start until jpegBuffer.size - 1) {
                if (jpegBuffer[i].toInt() == JPEG_DELIMITER_BYTES[0] &&
                    jpegBuffer[i + 1].toInt() == JPEG_DELIMITER_BYTES[1]) {
                    return i + 2
                }
            }

            // If we reach this, it means that no marker was found
            throw RuntimeException("Separator marker not found in buffer (${jpegBuffer.size})")
        }
    }
}
