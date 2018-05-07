import { PropTypes } from 'react';
import { requireNativeComponent, View } from 'react-native';

var iface = {
  name: 'AnnotationARView',
  propTypes: {
    lat: PropTypes.number,
    lon: PropTypes.number,
    heading: PropTypes.number,
    pitch: PropTypes.number,
    horizonSlope: PropTypes.number,
    ...View.propTypes // include the default view properties
  },
};

module.exports = requireNativeComponent('AnnotationARView', iface);
