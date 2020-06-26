export class Attachment {
  auctionId: number;
  mainPhotoId: number;
  isUserImagePhoto: boolean;
}

export class AttachmentToUpdate extends Attachment {
  idsToRemoved: number[];
  isMainPhotoAllReadySaved: boolean;
}
