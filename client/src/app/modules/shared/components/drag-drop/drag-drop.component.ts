import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {EnumsHelper, FileExtension} from '../../config/enums';

@Component({
  selector: 'app-drag-drop',
  templateUrl: './drag-drop.component.html',
  styleUrls: ['./drag-drop.component.scss']
})
export class DragDropComponent implements OnChanges {

  @Input() maxAmountOfImages: number;
  @Output() filesEmitter: EventEmitter<File[]> = new EventEmitter();
  files: File[];
  validatedFiles: File[];
  extensions = EnumsHelper.getValuesByEnumName(FileExtension);

  constructor(private http: HttpClient) {
    this.setEmptyArrays();
  }

  ngOnChanges(): void {
  }

  private setEmptyArrays(): void {
    this.files = [] as File[];
    this.validatedFiles = [] as File[];
  }

  fileProgress(fileInput: any): void {
    this.setFile(fileInput);
    this.addValidFilesToList();
    if (this.validatedFiles.length > 0) {
      this.filesEmitter.emit(this.validatedFiles);
    }
    this.setEmptyArrays();
  }

  private setFile(fileInput: any): void {
    for (let i = 0; i < this.maxAmountOfImages; i++) {
      if (fileInput.target && fileInput.target.files[i]) {
        this.files.push(fileInput.target.files[i] as File);
      } else if (fileInput[i]) {
        this.files.push(fileInput[i]);
      }
    }
  }

  private addValidFilesToList(): void {
    for (const file of this.files) {
      if (this.checkExtension(file) && this.checkFileSize(file)) {
        this.validatedFiles.push(file);
      }
    }
  }

  private checkExtension(file: File): boolean {
    const ext: string = this.getExtensionFromFile(file.name);
    return this.extensions.includes(ext);
  }

  private getExtensionFromFile(name: string): string {
    return name.split('.').pop();
  }

  private checkFileSize(file: File) {
    return file.size < 4000000;
  }
}
