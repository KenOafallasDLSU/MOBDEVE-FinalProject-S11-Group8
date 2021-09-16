let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => {
  localVideo.style.opacity = 1
}
remoteVideo.onplaying = () => {
  remoteVideo.style.opacity = 1
}

let peer
function init(userId) {
  peer = new Peer(userId)

  peer.on('open', () => {
    // call Kotlin function
  })

  listen()
}

let localStream
function listen() {
  peer.on('call', (call) => {

    navigator.getUserMedia({
      audio: true,
      video: true
      /*
      video: {
        frameRate: 24,
        width: {
          min: 480, ideal: 720, max: 1280
        }
        aspectRation: 1.333
      }
      */
    }, (stream) => {
      localVideo.srcObject = stream
      localStream = stream

      call.answer(stream)
      call.on('stream', (remoteStream) => {
        remoteVideo.srcObject = remoteStream
      
        remoteVideo.className = "primary-video"
        localVideo.className = "secondary-video"
      })

    })

  })
}

function startCall(otherUserId) {
  navigator.getUserMedia({
    audio: true,
    video: true
  }, (stream) => {

    localVideo.srcObject = stream
    localStream = stream

    const call = peer.call(otherUserId, stream)
    call.on('stream', (remoteStream) => {
      remoteVideo.srcObject = remoteStream
    
      remoteVideo.className = "primary-video"
      localVideo.className = "secondary-video"
    })

  })
}

const toggleVideo = (b) => {
  if (b == "true") {
    localStream.getVideoTracks()[0].enabled = true
  } else {
    localStream.getVideoTracks()[0].enabled = false
  }
}

const toggleAudio = (b) => {
  if (b == "true") {
    localStream.getAudioTracks()[0].enabled = true
  } else {
    localStream.getAudioTracks()[0].enabled = false
  }
}