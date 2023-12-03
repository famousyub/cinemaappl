import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';

export const BookDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">
          <Translate contentKey="myappApp.book.detail.title">Book</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{bookEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="myappApp.book.title">Title</Translate>
            </span>
          </dt>
          <dd>{bookEntity.title}</dd>
          <dt>
            <span id="descripton">
              <Translate contentKey="myappApp.book.descripton">Descripton</Translate>
            </span>
          </dt>
          <dd>{bookEntity.descripton}</dd>
          <dt>
            <span id="publicationDate">
              <Translate contentKey="myappApp.book.publicationDate">Publication Date</Translate>
            </span>
          </dt>
          <dd>
            {bookEntity.publicationDate ? (
              <TextFormat value={bookEntity.publicationDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="bookimage">
              <Translate contentKey="myappApp.book.bookimage">Bookimage</Translate>
            </span>
          </dt>
          <dd>
            {bookEntity.bookimage ? (
              <div>
                {bookEntity.bookimageContentType ? (
                  <a onClick={openFile(bookEntity.bookimageContentType, bookEntity.bookimage)}>
                    <img src={`data:${bookEntity.bookimageContentType};base64,${bookEntity.bookimage}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {bookEntity.bookimageContentType}, {byteSize(bookEntity.bookimage)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="myappApp.book.name">Name</Translate>
          </dt>
          <dd>{bookEntity.name ? bookEntity.name.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/book" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/book/${bookEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BookDetail;
