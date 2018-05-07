/**
 * React Natve AR Demo - A basic demo interface for displaying pins in augmented reality
 */

import React, {
  Component
} from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  DeviceEventEmitter
} from 'react-native';
import Camera from 'react-native-camera';
import OrientationManager from './OrientationManager';
import AnnotationARView from './AnnotationARView';

class ReactNativeARDemo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cameraType: Camera.constants.Type.back,
      heading: 0,
      pitch: 0,
      lat: 0.0,
      lon: 0.0,
    };
  }

  componentDidMount() {
    //
    // component state aware here - attach events
    //
    OrientationManager.start();

    DeviceEventEmitter.addListener('OrientationUpdate', (data) => {
      this.setState({
        pitch: data.pitch,
        heading: data.heading,
        lat: data.lat,
        lon: data.lon
      });
    });
  }

  componentWillUnMount(){
    OrientationManager.stop();
  }

  render() {
    return (
      <Camera
        ref="cam"
        style={styles.baseContainer}
      >
        <View style={styles.container}>

          <AnnotationARView style={styles.fullScreen} lat={this.state.lat} lon={this.state.lon}
              heading={this.state.heading} pitch={this.state.pitch}></AnnotationARView>

            <Text style={styles.welcome}>
              React Native AR Demo
            </Text>
            <Text style={styles.instructions}>
              Location ({this.state.lat}, {this.state.lon})
            </Text>
            <Text style={styles.instructions}>
              Heading {this.state.heading}
            </Text>
            <Text style={styles.instructions}>
              Pitch {this.state.pitch}
            </Text>
        </View>
      </Camera>
    );
  }
}

const styles = StyleSheet.create({
  baseContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
  },
  subContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  fullScreen: {
    //flex: 1,
    position: 'absolute',
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    top: 0
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
    color: '#F5FCFF'
  },
  instructions: {
    textAlign: 'center',
    color: '#F5FCFF',
    marginBottom: 5,
  },
  pinView: {
    position: 'absolute',
  }
});

AppRegistry.registerComponent('ReactNativeARDemo', () => ReactNativeARDemo);
