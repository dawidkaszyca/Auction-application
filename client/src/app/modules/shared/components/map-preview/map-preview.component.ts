import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {City} from '../../models/auction-base-field';
import {google} from 'google-maps';

@Component({
  selector: 'app-map-preview',
  templateUrl: './map-preview.component.html',
  styleUrls: ['./map-preview.component.scss']
})
export class MapPreviewComponent implements OnInit {

  @Input()
  city: City;
  mapElement;
  map: any;

  constructor() {
  }

  ngOnInit() {
    this.initMap();
  }

  initMap() {
    const coords = new google.maps.LatLng(Number(this.city.latitude), Number(this.city.longitude));
    const mapOptions: google.maps.MapOptions = {
      center: coords,
      zoom: 11,
      mapTypeControl: false,
      streetViewControl: false,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    this.mapElement = document.getElementById('map');
    this.map = new google.maps.Map(this.mapElement, mapOptions);
    new google.maps.Marker({
      map: this.map,
      position: coords
    });
  }
}
