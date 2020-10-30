export enum Kilometer {
  ZERO = '0',
  FIVE = '5',
  TEN = '10',
  FIFTEEN = '15',
  TWENTY = '20',
  THIRTY = '30',
  FIFTY = '50',
  HUNDRED = '100'
}

export enum FileExtension {
  JPG = 'jpg',
  PNG = 'png',
  JFIF = 'jfif'
}

export enum PageSize {
  TEN = '10',
  FIFTEEN = '15',
  TWENTY = '20',
  TWENTY_FIVE = '25',
  THIRTY = '30'
}

export enum Order {
  ASC = 'ASC',
  DESC = 'DESC',
}

export enum State {
  ALL = 'ALL',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
}

export enum StateKey {
  ACTIVE = 'myAuction.active',
  INACTIVE = 'myAuction.inactive',
}

export enum ReasonKey {
  SPAM = 'report.spam',
  INVALID_CONTENT = 'report.invalid-content',
  EXPIRED = 'report.expired',
  FRAUD = 'report.fraud',
}

export enum SortKey {
  MIN_PRICE_KEY = 'sort.minPrice',
  MAX_PRICE_KEY = 'sort.maxPrice',
  NEWEST_KEY = 'sort.newest',
  LATEST_KEY = 'sort.latest',
  POPULARITY_KEY = 'sort.popularity'
}

export enum SortField {
  PRICE = 'price',
  VIEWERS = 'viewers',
  CREATED_DATE = 'createdDate',
}


export enum DialogKey {
  AFTER_REPORT = 'dialog.after-report',
  AFTER_FAVORITE = 'dialog.after-favorite',
  AFTER_FAVORITE_ERROR = 'dialog.after-favorite-error',
  SAVE_AUCTION_ERROR = 'dialog.save-auction',
  UPDATE_AUCTION_ERROR = 'dialog.update-auction',
  REMOVE_AUCTION_ERROR = 'dialog.remove-auction',
  EXTEND_AUCTION_TIME = 'dialog.extend-time',
  EXTEND_AUCTION_TIME_ERROR = 'dialog.extend-time-error',
  SAVE_ATTACHMENT_ERROR = 'dialog.save-attachment',
  UPDATE_ATTACHMENT_ERROR = 'dialog.update-attachment'
}

export class EnumsHelper {
  static getValuesByEnumName(name: any): any[] {
    const array = [];
    for (const obj in name) {
      const value = name[obj];
      if (!isNaN(Number(value))) {
        array.push(Number(value));
      } else {
        array.push(value);
      }
    }
    return array;
  }
}
